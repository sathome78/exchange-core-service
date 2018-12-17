package me.exrates.controller.ngcontroller;

import me.exrates.controller.ngcontroller.exception.NgDashboardException;
import me.exrates.controller.ngcontroller.model.PasswordCreateDto;
import me.exrates.controller.ngcontroller.service.NgUserService;
import me.exrates.exception.IncorrectPasswordException;
import me.exrates.exception.UserNotFoundException;
import me.exrates.exception.security.exception.IncorrectPinException;
import me.exrates.model.AuthTokenDto;
import me.exrates.model.User;
import me.exrates.model.UserAuthenticationDto;
import me.exrates.model.dto.UserEmailDto;
import me.exrates.model.enums.UserStatus;
import me.exrates.model.error.ErrorInfo;
import me.exrates.service.AuthTokenService;
import me.exrates.service.G2faService;
import me.exrates.service.ReferralService;
import me.exrates.service.SecureService;
import me.exrates.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Locale;
import java.util.Optional;

import static org.apache.commons.lang.StringUtils.isEmpty;

@RestController
@RequestMapping(value = "/info/public/v2/users",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
)
public class NgUserController {

    private static final Logger logger = LogManager.getLogger(NgUserController.class);

    private final AuthTokenService authTokenService;
    private final UserService userService;
    private final ReferralService referralService;
    private final SecureService secureService;
    private final G2faService g2faService;
    private final NgUserService ngUserService;

    @Value("${dev.mode}")
    private boolean DEV_MODE;

    @Autowired
    public NgUserController(AuthTokenService authTokenService,
                            UserService userService,
                            ReferralService referralService,
                            SecureService secureService,
                            G2faService g2faService,
                            NgUserService ngUserService) {
        this.authTokenService = authTokenService;
        this.userService = userService;
        this.referralService = referralService;
        this.secureService = secureService;
        this.g2faService = g2faService;
        this.ngUserService = ngUserService;
    }

    @PostMapping(value = "/authenticate")
    public ResponseEntity<AuthTokenDto> authenticate(@RequestBody @Valid UserAuthenticationDto authenticationDto,
                                                     HttpServletRequest request) throws Exception {
        logger.info("authenticate, email = {}, ip = {}", authenticationDto.getEmail(),
                authenticationDto.getClientIp());

        if (authenticationDto.getEmail().startsWith("promo@ex") ||
                authenticationDto.getEmail().startsWith("dev@exrat")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);   // 403
        }
        User user;
        try {
            user = userService.findByEmail(authenticationDto.getEmail());
        } catch (UserNotFoundException esc) {
            logger.debug("User with email {} not found", authenticationDto.getEmail());
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);  // 422
        }

        if (user.getStatus() == UserStatus.REGISTERED) {
            return new ResponseEntity<>(HttpStatus.UPGRADE_REQUIRED); // 426
        }
        if (user.getStatus() == UserStatus.DELETED) {
            return new ResponseEntity<>(HttpStatus.GONE); // 410
        }
        boolean shouldLoginWithGoogle = g2faService.isGoogleAuthenticatorEnable(user.getId());

        if (!DEV_MODE) {

            if (isEmpty(authenticationDto.getPin())) {

                if (!shouldLoginWithGoogle) {
                    secureService.sendLoginPincode(user, request);
                }
                return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT); //418
            }
        }
        authenticationDto.setPinRequired(true);
        Optional<AuthTokenDto> authTokenResult;
        try {
            authTokenResult = authTokenService.retrieveTokenNg(request, authenticationDto,
                    authenticationDto.getClientIp(), shouldLoginWithGoogle);
        } catch (IncorrectPinException wrongPin) {
            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT); //418
        } catch (UsernameNotFoundException | IncorrectPasswordException e) {
//            ipBlockingService.failureProcessing(authenticationDto.getClientIp(), IpTypesOfChecking.LOGIN);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401
        }
        AuthTokenDto authTokenDto =
                authTokenResult.orElseThrow(() -> new Exception("Failed to authenticate user with email: " + authenticationDto.getEmail()));

        authTokenDto.setNickname(user.getNickname());
        authTokenDto.setUserId(user.getId());
        authTokenDto.setLocale(new Locale(userService.getPreferedLang(user.getId())));
        String avatarLogicalPath = userService.getAvatarPath(user.getId());
        String avatarFullPath = avatarLogicalPath == null || avatarLogicalPath.isEmpty() ? null : getAvatarPathPrefix(request) + avatarLogicalPath;
        authTokenDto.setAvatarPath(avatarFullPath);
        authTokenDto.setFinPasswordSet(user.getFinpassword() != null);
        authTokenDto.setReferralReference(referralService.generateReferral(user.getEmail()));
//        ipBlockingService.successfulProcessing(authenticationDto.getClientIp(), IpTypesOfChecking.LOGIN);
        return ResponseEntity.ok(authTokenDto); // 200
    }

    @PostMapping(value = "/register")
    public ResponseEntity register(@RequestBody @Valid UserEmailDto userEmailDto, HttpServletRequest request) {
        boolean result = ngUserService.registerUser(userEmailDto, request);
        if (result) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    private String getAvatarPathPrefix(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() +
                ":" + request.getServerPort() + "/rest";
    }

    @PostMapping("/createPassword")
    public ResponseEntity savePassword(@RequestBody @Valid PasswordCreateDto passwordCreateDto,
                                       HttpServletRequest request) {
        AuthTokenDto tokenDto = ngUserService.createPassword(passwordCreateDto, request);
        return ResponseEntity.ok(tokenDto);
    }

    @PostMapping("/recoveryPassword")
    public ResponseEntity recoveryPassword(@RequestBody @Valid UserEmailDto userEmailDto, HttpServletRequest request) {
        boolean result = ngUserService.recoveryPassword(userEmailDto, request);
        return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NgDashboardException.class)
    @ResponseBody
    public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }
}
