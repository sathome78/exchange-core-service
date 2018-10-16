package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
public class UserSessionService {

    @Autowired
    @Qualifier("ExratesSessionRegistry")
    private SessionRegistry sessionRegistry;

    public void invalidateUserSessionExceptSpecific(String userEmail, String specificSessionId) {
        Optional<Object> updatedUser = sessionRegistry.getAllPrincipals().stream()
                .filter(principalObj -> {
                    UserDetails principal = (UserDetails) principalObj;
                    return userEmail.equals(principal.getUsername());
                })
                .findFirst();
        updatedUser.ifPresent(o -> sessionRegistry.getAllSessions(o, false).stream().filter(session -> session.getSessionId() != specificSessionId).collect(Collectors.toList()).forEach(SessionInformation::expireNow));
    }

}
