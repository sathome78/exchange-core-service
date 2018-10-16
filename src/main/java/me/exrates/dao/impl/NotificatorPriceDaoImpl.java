package me.exrates.dao.impl;

import me.exrates.dao.NotificatorPriceDao;
import me.exrates.model.dto.NotificatorTotalPriceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public class NotificatorPriceDaoImpl implements NotificatorPriceDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public BigDecimal getFeeMessagePrice(int notificatorId, int roleId) {
        final String sql = "SELECT message_price FROM 2FA_NOTIFICATION_PRICE " +
                "WHERE notificator_id = :notificator_id AND role_id = :role_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue( "notificator_id", notificatorId);
        params.addValue("role_id", roleId);
        return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
    }

    @Override
    public NotificatorTotalPriceDto getPrices(int notificatorId, int roleId) {
        final String sql = "SELECT * FROM 2FA_NOTIFICATION_PRICE " +
                " WHERE notificator_id = :notificator_id AND role_id = :role_id ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("notificator_id", notificatorId);
        params.addValue("role_id", roleId);
        return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
            NotificatorTotalPriceDto notificatorTotalPriceDto = new NotificatorTotalPriceDto();
            notificatorTotalPriceDto.setMessagePrice(rs.getString("message_price"));
            notificatorTotalPriceDto.setSubscriptionPrice(rs.getString("subscribe_price"));
            return notificatorTotalPriceDto;
        });
    }

    @Override
    public BigDecimal getSubscriptionPrice(int notificatorId, int roleId) {
        final String sql = "SELECT subscribe_price FROM 2FA_NOTIFICATION_PRICE " +
                "WHERE notificator_id = :notificator_id AND role_id = :role_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("notificator_id", notificatorId);
        params.addValue("role_id", roleId);
        return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
    }


    @Override
    public int updatePrice(BigDecimal price, int roleId, int notificatorId, String priceColumn) {
        final String sql = String.format("UPDATE 2FA_NOTIFICATION_PRICE SET %s = :price " +
                "WHERE notificator_id = :notificator_id AND role_id = :role_id", priceColumn);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("notificator_id", notificatorId);
        params.addValue("role_id", roleId);
        params.addValue("price", price);
        return jdbcTemplate.update(sql, params);
    }
}
