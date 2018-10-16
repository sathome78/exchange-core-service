package me.exrates.dao.impl;

import me.exrates.dao.SessionParamsDao;
import me.exrates.model.SessionLifeTimeType;
import me.exrates.model.SessionParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maks on 31.03.2017.
 */
@Repository
public class SessionParamsDaoImpl implements SessionParamsDao {

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private RowMapper<SessionParams> getSessionParamsRowMapper() {
        return (resultSet, i) -> {
            final SessionParams sessionParams = new SessionParams();
            sessionParams.setId(resultSet.getInt("id"));
            sessionParams.setUserId(resultSet.getInt("user_id"));
            sessionParams.setSessionTimeMinutes(resultSet.getInt("session_time_minutes"));
            sessionParams.setSessionLifeTypeId(resultSet.getInt("session_life_type_id"));
            return sessionParams;
        };
    }

    private RowMapper<SessionLifeTimeType> getSessionLifeTimeTypeRowMapper() {
        return (resultSet, i) -> {
            final SessionLifeTimeType sessionLifeTimeType = new SessionLifeTimeType();
            sessionLifeTimeType.setId(resultSet.getInt("id"));
            sessionLifeTimeType.setName(resultSet.getString("name"));
            sessionLifeTimeType.setAvailable(resultSet.getBoolean("active"));
            return sessionLifeTimeType;
        };
    }

    @Override
    public List<SessionLifeTimeType> getAllByActive(boolean active) {
        String sql = "SELECT * FROM SESSION_LIFE_TIME_TYPE AS SP " +
                "WHERE SP.active = :active";
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("active", active);
        try {
            return namedParameterJdbcTemplate.query(sql, namedParameters, getSessionLifeTimeTypeRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    @Override
    public SessionParams getByUserEmail(String userEmail) {
        String sql = "SELECT * FROM SESSION_PARAMS AS SP " +
                "INNER JOIN USER AS U ON U.id = SP.user_id " +
                "WHERE U.email = :email";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("email", userEmail);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, getSessionParamsRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public SessionParams create(SessionParams sessionParams) {
        String sql = "INSERT INTO SESSION_PARAMS" +
                " (user_id, session_time_minutes, session_life_type_id)" +
                " VALUES" +
                " (:user_id, :session_time_minutes, :session_life_type_id)";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("user_id", sessionParams.getUserId());
            put("session_time_minutes", sessionParams.getSessionTimeMinutes());
            put("session_life_type_id", sessionParams.getSessionLifeTypeId());
        }};
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int result = namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);
        final int id = (int) keyHolder.getKey().longValue();
        sessionParams.setId(id);
        if (result <= 0) {
            return null;
        }
        return sessionParams;
    }

    @Override
    public void update(SessionParams sessionParams) {
        final String sql = "UPDATE SESSION_PARAMS " +
                "SET session_time_minutes = :session_time_minutes, " +
                "session_life_type_id = :session_life_type_id " +
                "WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", sessionParams.getId());
        params.put("session_time_minutes", sessionParams.getSessionTimeMinutes());
        params.put("session_life_type_id", sessionParams.getSessionLifeTypeId());
        namedParameterJdbcTemplate.update(sql, params);
    }
}
