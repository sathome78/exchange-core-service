package me.exrates.service.impl;

import me.exrates.dao.TemporalTokenDao;
import me.exrates.model.dto.TemporalToken;
import me.exrates.service.TemporalTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemporalTokenServiceImpl implements TemporalTokenService {
    @Autowired
    TemporalTokenDao temporalTokenDao;
    @Override
    public boolean updateTemporalToken(TemporalToken temporalToken) {
        return temporalTokenDao.updateTemporalToken(temporalToken);
    }

    public void deleteTemporalToken(String temporalToken) {
        temporalTokenDao.deleteTemporalToken(temporalToken);
    }

}
