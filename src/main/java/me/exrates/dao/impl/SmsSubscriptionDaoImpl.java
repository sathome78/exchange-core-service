package me.exrates.dao.impl;

import me.exrates.dao.SmsSubscriptionDao;
import me.exrates.model.dto.SmsSubscriptionDto;
import me.exrates.model.enums.NotificatorSubscriptionStateEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Repository
public class SmsSubscriptionDaoImpl implements SmsSubscriptionDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public int create(SmsSubscriptionDto dto) {
        final String sql = "INSERT INTO SMS_SUBSCRIPTION (user_id, contact, subscription_state, " +
                "subscribe_code, delivery_price, new_price, new_contact) VALUES " +
                "(:user_id, :contact, :subscription_state, :subscribe_code, :delivery_price, :new_price, :new_contact)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", dto.getUserId());
        params.addValue("new_contact", dto.getNewContact());
        params.addValue("subscription_state", dto.getStateEnum().name());
        params.addValue("subscribe_code", dto.getCode());
        params.addValue("new_price", dto.getNewPrice());
        params.addValue("delivery_price", dto.getPriceForContact());
        params.addValue("contact", dto.getContact());
        jdbcTemplate.update(sql, params, keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public void update(SmsSubscriptionDto dto) {
        final String sql = " UPDATE SMS_SUBSCRIPTION " +
                " SET contact = :contact, subscription_state = :subscription_state, " +
                " subscribe_code = :subscribe_code, delivery_price = :delivery_price, new_price = :new_price, " +
                " new_contact = :new_contact" +
                " WHERE user_id = :user_id ";
        Map<String, Object> params = new HashMap<>();
        params.put("subscription_state", dto.getStateEnum().name());
        params.put("subscribe_code", dto.getCode());
        params.put("delivery_price", dto.getPriceForContact());
        params.put("contact", dto.getContact());
        params.put("user_id", dto.getUserId());
        params.put("new_price", dto.getNewPrice());
        params.put("new_contact", dto.getNewContact());
        jdbcTemplate.update(sql, params);
    }

    @Override
    public SmsSubscriptionDto getByUserId(int userId) {
        final String sql = "SELECT * FROM SMS_SUBSCRIPTION " +
                "WHERE user_id = :user_id";
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        try {
            return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
                SmsSubscriptionDto dto = new SmsSubscriptionDto();
                dto.setUserId(rs.getInt("user_id"));
                dto.setContact(rs.getString("contact"));
                dto.setCode(rs.getString("subscribe_code"));
                dto.setPriceForContact(rs.getBigDecimal("delivery_price"));
                dto.setStateEnum(NotificatorSubscriptionStateEnum.valueOf(
                        rs.getString("subscription_state")));
                dto.setId(rs.getInt("id"));
                dto.setNewPrice(rs.getBigDecimal("new_price"));
                dto.setNewContact(rs.getString("new_contact"));
                return dto;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void updateDeliveryPrice(int userId, BigDecimal cost) {
        final String sql = " UPDATE SMS_SUBSCRIPTION " +
                " SET delivery_price = :delivery_price " +
                " WHERE user_id = :user_id ";
        Map<String, Object> params = new HashMap<>();
        params.put("delivery_price", cost);
        params.put("user_id", userId);
        jdbcTemplate.update(sql, params);
    }
}
