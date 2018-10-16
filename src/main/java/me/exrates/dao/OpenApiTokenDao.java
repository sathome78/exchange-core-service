package me.exrates.dao;

import me.exrates.model.dto.OpenApiTokenPublicDto;
import me.exrates.model.main.OpenApiToken;

import java.util.List;
import java.util.Optional;

public interface OpenApiTokenDao {
    List<OpenApiTokenPublicDto> getActiveTokensForUser(String userEmail);

    Optional<OpenApiToken> getById(Long tokenId);

    void updateToken(Long tokenId, String alias, Boolean allowTrade, Boolean allowWithdraw);

    void deactivateToken(Long tokenId);

    Long saveToken(OpenApiToken token);
}
