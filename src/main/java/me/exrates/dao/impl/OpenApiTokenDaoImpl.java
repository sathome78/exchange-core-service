package me.exrates.dao.impl;

import me.exrates.dao.OpenApiTokenDao;
import me.exrates.model.dto.OpenApiTokenPublicDto;
import me.exrates.model.main.OpenApiToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OpenApiTokenDaoImpl implements OpenApiTokenDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public List<OpenApiTokenPublicDto> getActiveTokensForUser(String userEmail) {
        String sql = "SELECT OT.id AS token_id, OT.user_id, OT.alias, OT.public_key, OT.allow_trade, OT.allow_withdraw," +
                " OT.private_key, OT.date_generation " +
                " FROM OPEN_API_USER_TOKEN OT" +
                " WHERE OT.is_active = 1 AND OT.user_id = (SELECT id FROM USER where email = :user_email) ";

        return jdbcTemplate.query(sql, Collections.singletonMap("user_email", userEmail), (rs, rowNum) -> {
            OpenApiTokenPublicDto token = new OpenApiTokenPublicDto();
            token.setId(rs.getLong("token_id"));
            token.setUserId(rs.getInt("user_id"));
            token.setAlias(rs.getString("alias"));
            token.setPublicKey(rs.getString("public_key"));
            token.setAllowTrade(rs.getBoolean("allow_trade"));
            token.setAllowWithdraw(rs.getBoolean("allow_withdraw"));
            token.setGenerationDate(rs.getTimestamp("date_generation").toLocalDateTime());
            return token;
        });
    }

    public Optional<OpenApiToken> getById(Long id) {
        String sql = "SELECT OT.id AS token_id, OT.user_id, U.email, OT.alias, OT.public_key, OT.private_key, OT.date_generation, " +
                "OT.allow_trade, OT.allow_withdraw " +
                " FROM OPEN_API_USER_TOKEN OT" +
                " JOIN USER U ON OT.user_id = U.id " +
                " WHERE OT.id = :id ";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, Collections.singletonMap("id", id), new OpenApiToken()));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void updateToken(Long tokenId, String alias, Boolean allowTrade, Boolean allowWithdraw) {
        String sql = "UPDATE OPEN_API_USER_TOKEN SET alias = :alias, allow_trade = :allow_trade, allow_withdraw = :allow_withdraw " +
                " WHERE id = :token_id ";
        Map<String, Object> params = new HashMap<>();
        params.put("token_id", tokenId);
        params.put("alias", alias);
        params.put("allow_trade", allowTrade);
        params.put("allow_withdraw", allowWithdraw);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void deactivateToken(Long tokenId) {
        String sql = "UPDATE OPEN_API_USER_TOKEN SET is_active = 0 WHERE id = :token_id ";
        jdbcTemplate.update(sql, Collections.singletonMap("token_id", tokenId));
    }

    @Override
    public Long saveToken(OpenApiToken token) {
        String sql = "INSERT INTO OPEN_API_USER_TOKEN (user_id, alias, public_key, private_key, allow_trade, allow_withdraw) " +
                " VALUES (:user_id, :alias, :public_key, :private_key, :allow_trade, :allow_withdraw) ";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", token.getUserId())
                .addValue("alias", token.getAlias())
                .addValue("public_key", token.getPublicKey())
                .addValue("private_key", token.getPrivateKey())
                .addValue("allow_trade", token.getAllowTrade())
                .addValue("allow_withdraw", token.getAllowWithdraw());
        jdbcTemplate.update(sql, params, keyHolder);
        return keyHolder.getKey().longValue();

    }
}
