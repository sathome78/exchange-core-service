package me.exrates.dao.impl;

import me.exrates.dao.TemporalTokenDao;
import me.exrates.model.dto.TemporalToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class TemporalTokenDaoImpl implements TemporalTokenDao {


    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public boolean updateTemporalToken(TemporalToken token) {
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("value", token.getValue());
        namedParameters.put("user_id", String.valueOf(token.getUserId()));
        namedParameters.put("id", String.valueOf(token.getId()));
        return namedParameterJdbcTemplate.update(UPDATE_BY_VALUE, namedParameters) > 0;
    }


    @Override
    public void deleteTemporalToken(String temporalToken) {
        Map<String, String> parametersMap = new HashMap<>();
        parametersMap.put("token_value", temporalToken);
        namedParameterJdbcTemplate.update(DELETE_BY_VALUE, parametersMap);
    }
}
