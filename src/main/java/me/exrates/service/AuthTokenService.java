package me.exrates.service;

import me.exrates.model.AuthTokenDto;
import me.exrates.model.UserAuthenticationDto;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public interface AuthTokenService {
    Optional<AuthTokenDto> retrieveTokenNg(HttpServletRequest request, UserAuthenticationDto dto, String clientIp,
                                           boolean isGoogleTwoFAEnabled);

    UserDetails getUserByToken(String token, String ip);

    @Scheduled(fixedDelay = 24L * 60L * 60L * 1000L, initialDelay = 60000L)
    void deleteExpiredTokens();

    boolean isValid(HttpServletRequest request);

    Optional<AuthTokenDto> retrieveTokenNg(String email, HttpServletRequest request);

}
