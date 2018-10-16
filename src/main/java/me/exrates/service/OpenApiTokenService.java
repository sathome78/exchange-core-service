package me.exrates.service;

import me.exrates.model.dto.OpenApiTokenPublicDto;
import me.exrates.model.main.OpenApiToken;

import java.util.List;

public interface OpenApiTokenService {
    List<OpenApiTokenPublicDto> getUserTokens(String name);

    OpenApiToken generateToken(String name, String alias);

    void updateToken(Long tokenId, Boolean allowTrade, String name);

    void deleteToken(Long tokenId, String name);
}
