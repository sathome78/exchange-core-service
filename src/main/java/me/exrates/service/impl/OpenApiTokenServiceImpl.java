package me.exrates.service.impl;

import me.exrates.dao.OpenApiTokenDao;
import me.exrates.exception.TokenAccessDeniedException;
import me.exrates.exception.TokenNotFoundException;
import me.exrates.model.dto.OpenApiTokenPublicDto;
import me.exrates.model.main.OpenApiToken;
import me.exrates.service.OpenApiTokenService;
import me.exrates.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static me.exrates.util.UtilTokenService.generateKey;

@Service
public class OpenApiTokenServiceImpl implements OpenApiTokenService {

    @Autowired
    private OpenApiTokenDao openApiTokenDao;

    @Autowired
    private UserService userService;

    @Value("${alias.regex}")
    private String aliasRegex;

    public List<OpenApiTokenPublicDto> getUserTokens(String userEmail) {
        return openApiTokenDao.getActiveTokensForUser(userEmail);
    }

    public OpenApiToken generateToken(String userEmail, String alias) {
        if (StringUtils.isEmpty(alias) || !alias.matches(aliasRegex)) {
            throw new IllegalArgumentException("Incorrect alias");
        }
        OpenApiToken token = new OpenApiToken();
        token.setUserEmail(userEmail);
        token.setUserId(userService.getIdByEmail(userEmail));
        token.setPublicKey(generateKey());
        token.setPrivateKey(generateKey());
        token.setAlias(alias);
        openApiTokenDao.saveToken(token);
        return token;
    }

    private void checkUser(String currentUserEmail, OpenApiToken token) {
        if (!currentUserEmail.equals(token.getUserEmail())) {
            throw new TokenAccessDeniedException("Access to token is forbidden");
        }
    }

    public void updateToken(Long tokenId, Boolean allowTrade, String currentUserEmail) {
        OpenApiToken token = openApiTokenDao.getById(tokenId).orElseThrow(() -> new TokenNotFoundException("Token not found by id: " + tokenId));
        checkUser(currentUserEmail, token);
        openApiTokenDao.updateToken(tokenId, token.getAlias(), allowTrade, token.getAllowWithdraw());
    }

    public void deleteToken(Long tokenId, String currentUserEmail) {
        OpenApiToken token = openApiTokenDao.getById(tokenId).orElseThrow(() -> new TokenNotFoundException("Token not found by id: " + tokenId));
        checkUser(currentUserEmail, token);
        openApiTokenDao.deactivateToken(tokenId);
    }
}
