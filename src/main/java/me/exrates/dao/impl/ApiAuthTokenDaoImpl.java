package me.exrates.dao.impl;

import lombok.NoArgsConstructor;
import me.exrates.dao.ApiAuthTokenDao;
import me.exrates.model.ApiAuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonMap;

@Repository
@NoArgsConstructor
public class ApiAuthTokenDaoImpl implements ApiAuthTokenDao {


    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public ApiAuthTokenDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public long createToken(ApiAuthToken token) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("username", token.getUsername())
                .addValue("value", token.getValue());
        int result = namedParameterJdbcTemplate.update(INSERT_API_AUTH_TOKEN, params, keyHolder);
        long id = keyHolder.getKey().longValue();
        if (result <= 0) {
            id = 0;
        }
        return id;
    }

    @Override
    public Optional<ApiAuthToken> retrieveTokenById(Long id) {
        Map<String, Long> params = singletonMap("id", id);
        try {
            return Optional.of(namedParameterJdbcTemplate.queryForObject(SELECT_TOKEN_BY_ID, params, new ApiAuthTokenRowMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean prolongToken(Long id) {
        String sql = "UPDATE API_AUTH_TOKEN SET last_request = CURRENT_TIMESTAMP WHERE id = :id";
        Map<String, Object> params = singletonMap("id", id);
        return namedParameterJdbcTemplate.update(sql, params) == 1;
    }

    @Override
    public boolean deleteExpiredToken(Long id) {
        String sql = "DELETE FROM API_AUTH_TOKEN WHERE id = :id";
        Map<String, Object> params = singletonMap("id", id);
        return namedParameterJdbcTemplate.update(sql, params) == 1;
    }

    @Override
    public int deleteAllExpired(long tokenDuration) {
        String sql = "DELETE FROM API_AUTH_TOKEN WHERE DATE_ADD(last_request,INTERVAL :duration SECOND) < now()";
        return namedParameterJdbcTemplate.update(sql, singletonMap("duration", tokenDuration));
    }

    public class ApiAuthTokenRowMapper implements RowMapper<ApiAuthToken> {

        public ApiAuthToken mapRow(ResultSet rs, int rowNum) throws SQLException {
            ApiAuthToken token = new ApiAuthToken();
            token.setId(rs.getLong("id"));
            token.setUsername(rs.getString("username"));
            token.setValue(rs.getString("value"));
            token.setLastRequest(rs.getTimestamp("last_request").toLocalDateTime());
            return token;
        }
    }

}
