package me.exrates.service.impl;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.ApiAuthTokenDao;
import me.exrates.exception.IncorrectPasswordException;
import me.exrates.exception.MissingCredentialException;
import me.exrates.exception.security.exception.IncorrectPinException;
import me.exrates.model.ApiAuthToken;
import me.exrates.model.AuthTokenDto;
import me.exrates.model.SessionParams;
import me.exrates.model.UserAuthenticationDto;
import me.exrates.model.dto.PinDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.service.*;
import me.exrates.service.utils.RestApiUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Service
@PropertySource(value = {"classpath:/mobile.properties", "classpath:/angular.properties"})
public class AuthTokenServiceImpl implements AuthTokenService {
    private static final Logger logger = LogManager.getLogger("mobileAPI");
    private static final int PIN_WAIT_MINUTES = 20;
    @Value("${token.key}")
    private String TOKEN_KEY;
    @Value("${token.duration}")
    private long TOKEN_DURATION_TIME;
    @Value("${token.max.duration}")
    private long TOKEN_MAX_DURATION_TIME;
    @Value("${dev.mode}")
    private boolean DEV_MODE;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private G2faService g2faService;

    @Autowired
    private SecureService secureService;

    @Autowired
    private ApiAuthTokenDao apiAuthTokenDao;

    @Autowired
    private SessionParamsService sessionParamsService;

    private Map<String, LocalDateTime> usersForPincheck = new ConcurrentHashMap<>();

    @Override
    public Optional<AuthTokenDto> retrieveTokenNg(HttpServletRequest request, UserAuthenticationDto dto, String clientIp,
                                                  boolean isGoogleTwoFAEnabled) {
        if (dto.getEmail() == null || dto.getPassword() == null) {
            throw new MissingCredentialException("Credentials missing");
        }
        String password = RestApiUtils.decodePassword(dto.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getEmail());
        logger.error("PASSWORD ENCODED: {}", passwordEncoder.encode(password));
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new IncorrectPasswordException("Incorrect password");
        }

        if (isGoogleTwoFAEnabled) {
            Integer userId = userService.getIdByEmail(dto.getEmail());
            if (!g2faService.checkGoogle2faVerifyCode(dto.getPin(), userId)) {
                if (!DEV_MODE) {
                    throw new IncorrectPinException("Incorrect google auth code");
                }
            }
        } else if (!DEV_MODE) {
            if (!userService.checkPin(dto.getEmail(), dto.getPin(), NotificationMessageEventEnum.LOGIN)) {
                PinDto res = secureService.reSendLoginMessage(request, dto.getEmail(), true);
                throw new IncorrectPinException(res);
            }
        }
        return prepareAuthTokenNg(userDetails, request, clientIp);
    }

    private Optional<AuthTokenDto> prepareAuthTokenNg(UserDetails userDetails, HttpServletRequest request, String clientIp) {
        ApiAuthToken token = createAuthToken(userDetails.getUsername());
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("token_id", token.getId());
        tokenData.put("client_ip", clientIp);
        tokenData.put("username", token.getUsername());
        tokenData.put("value", token.getValue());
        JwtBuilder jwtBuilder = Jwts.builder();
        Optional<SessionParams> params = Optional.of(sessionParamsService.getByEmailOrDefault(userDetails.getUsername()));
        Date expiration = params
                .map(p -> getExpirationTime(p.getSessionTimeMinutes()))
                .orElseGet(() -> getExpirationTime(TOKEN_MAX_DURATION_TIME / 60));
        tokenData.put("expiration", expiration.getTime());
        jwtBuilder.setClaims(tokenData);
        AuthTokenDto authTokenDto = new AuthTokenDto(jwtBuilder.signWith(SignatureAlgorithm.HS512, TOKEN_KEY).compact());
        usersForPincheck.remove(token.getUsername());
        return Optional.of(authTokenDto);
    }


    private Date getExpirationTime(long minutes) {
        return Date.from(LocalDateTime.now().plusMinutes(minutes).atZone(ZoneId.systemDefault()).toInstant());
    }

    private ApiAuthToken createAuthToken(String username) {
        ApiAuthToken token = new ApiAuthToken();
        token.setUsername(username);
        token.setValue(UUID.randomUUID().toString());
        Long id = apiAuthTokenDao.createToken(token);
        token.setId(id);
        return token;

    }

    @Override
    public UserDetails getUserByToken(String token, String ip) {
        return null;
    }

    @Override
    public void deleteExpiredTokens() {

    }

    @Override
    public boolean isValid(HttpServletRequest request) {
        return false;
    }

    @Override
    public Optional<AuthTokenDto> retrieveTokenNg(String email, HttpServletRequest request) {
        return Optional.empty();
    }
}
