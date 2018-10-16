package me.exrates.dao.impl;

import me.exrates.dao.ReferralLevelDao;
import me.exrates.model.ReferralLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonMap;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public class ReferralLevelDaoImpl implements ReferralLevelDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    protected static RowMapper<ReferralLevel> referralLevelRowMapper = (resultSet, i) -> {
        final ReferralLevel result = new ReferralLevel();
        result.setPercent(resultSet.getBigDecimal("REFERRAL_LEVEL.percent"));
        result.setLevel(resultSet.getInt("REFERRAL_LEVEL.level"));
        result.setId(resultSet.getInt("REFERRAL_LEVEL.id"));
        return result;
    };

    @Autowired
    public ReferralLevelDaoImpl(@Qualifier(value = "masterTemplate")final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ReferralLevel> findAll() {
        final String sql = "SELECT REFERRAL_LEVEL.*  FROM REFERRAL_LEVEL REFERRAL_LEVEL LEFT JOIN REFERRAL_LEVEL b ON REFERRAL_LEVEL.level = b.level AND REFERRAL_LEVEL.datetime < b.datetime WHERE b.datetime is NULL ORDER BY level;";
        try {
            return jdbcTemplate.query(sql, referralLevelRowMapper);
        } catch (final EmptyResultDataAccessException ignore) {
            return emptyList();
        }
    }

    @Override
    public ReferralLevel findById(final int id) {
        final String sql = "SELECT * FROM REFERRAL_LEVEL where id = :id";
        final Map<String, Integer> params = singletonMap("id", id);
        return jdbcTemplate.queryForObject(sql, params, referralLevelRowMapper);
    }

    @Override
    public BigDecimal getTotalLevelsPercent() {
        final String sql = "SELECT SUM(o.percent) as amount FROM REFERRAL_LEVEL o LEFT JOIN REFERRAL_LEVEL b ON o.level = b.level AND o.datetime < b.datetime WHERE b.datetime is NULL";
        return jdbcTemplate.query(sql, resultSet ->  {
            if (resultSet.next()) {
                return resultSet.getBigDecimal("amount");
            }
            return BigDecimal.ZERO;
        });
    }

    @Override
    public int create(final ReferralLevel level) {
        final String sql = "INSERT INTO REFERRAL_LEVEL (level, percent) VALUES (:level, :percent)";
        final Map<String, Object> params = new HashMap<>();
        final KeyHolder holder = new GeneratedKeyHolder();
        params.put("level", level.getLevel());
        params.put("percent", level.getPercent());
        jdbcTemplate.update(sql, new MapSqlParameterSource(params), holder);
        return holder.getKey().intValue();
    }

    @Override
    public void delete(final int levelId) {
        final String sql = "DELETE FROM REFERRAL_LEVEL where id = :id";
        final Map<String, Integer> params = singletonMap("id", levelId);
        jdbcTemplate.update(sql, params);
    }
}
