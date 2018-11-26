package me.exrates.dao;

import me.exrates.service.DashboardDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Repository
public class DashboardDaoImpl implements DashboardDao {

    @Autowired
    @Qualifier("masterHikariDataSource")
    DataSource dataSource;

    @Override
    public BigDecimal getBalanceByCurrency(int userId, int currencyId) {
        String sql = "SELECT active_balance FROM WALLET WHERE user_id = :userId AND currency_id = :currencyId;";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("userId", String.valueOf(userId));
        namedParameters.put("currencyId", String.valueOf(currencyId));
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, BigDecimal.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

}
