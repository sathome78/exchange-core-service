package me.exrates.service;

import me.exrates.model.dto.TemporalToken;

public interface TemporalTokenService {

    boolean updateTemporalToken(TemporalToken temporalToken);

    void deleteTemporalToken(String temporalToken);
}
