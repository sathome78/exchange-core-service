package me.exrates.dao.impl;

import me.exrates.dao.TelegramSubscriptionDao;
import me.exrates.model.dto.TelegramSubscription;
import me.exrates.model.enums.NotificatorSubscriptionStateEnum;
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
import java.util.Map;
import java.util.Optional;

/**
 * Created by Maks on 05.10.2017.
 */
@Repository
public class TelegramSubscriptionDaoImpl implements TelegramSubscriptionDao {

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;

    private static RowMapper<TelegramSubscription> telegramSubscribtionRowMapper = (rs, idx) -> {
        TelegramSubscription subscription = new TelegramSubscription();
        subscription.setId(rs.getInt("id"));
        subscription.setChatId(rs.getLong("chat_id"));
        subscription.setUserId(rs.getInt("user_id"));
        subscription.setUserAccount(rs.getString("user_account"));
        subscription.setCode(rs.getString("code"));
        subscription.setSubscriptionState(NotificatorSubscriptionStateEnum.valueOf(rs.getString("subscription_state")));
        return subscription;
    };


    @Override
    public Optional<TelegramSubscription> getSubscribtionByCodeAndEmail(String code, String email) {
        final String sql = " SELECT * FROM TELEGRAM_SUBSCRIPTION " +
                " INNER JOIN USER U ON U.email = :email " +
                " WHERE code = :code ";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("code", code)
                .addValue("email", email);
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, params, telegramSubscribtionRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public TelegramSubscription getSubscribtionByUserId(int userId) {
        final String sql = " SELECT * FROM TELEGRAM_SUBSCRIPTION WHERE user_id = :id ";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", userId);
        try{
            return jdbcTemplate.queryForObject(sql, params, telegramSubscribtionRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void updateSubscription(TelegramSubscription subscribtion) {
        final String sql = " UPDATE TELEGRAM_SUBSCRIPTION " +
                " SET code = :code, subscription_state = :subscription_state, user_account = :user_account, chat_id = :chat_id " +
                " WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", subscribtion.getId());
        params.put("code", subscribtion.getCode());
        params.put("subscription_state", subscribtion.getSubscriptionState().name());
        params.put("user_account", subscribtion.getUserAccount());
        params.put("chat_id", subscribtion.getChatId());
        jdbcTemplate.update(sql, params);
    }

    @Override
    public int create(TelegramSubscription subscription) {
        final String sql = " INSERT INTO TELEGRAM_SUBSCRIPTION (user_id, chat_id, subscription_state, user_account, code)" +
                " VALUES (:user_id, :chat_id, :state, :user_account, :code) ";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", subscription.getUserId())
                .addValue("chat_id", subscription.getChatId())
                .addValue("state", subscription.getSubscriptionState().name())
                .addValue("user_account", subscription.getUserAccount())
                .addValue("code", subscription.getCode());
        jdbcTemplate.update(sql, params, keyHolder);
        return (int) keyHolder.getKey().longValue();
    }

    @Override
    public void updateCode(String code, int userId) {
        final String sql = " UPDATE TELEGRAM_SUBSCRIPTION " +
                " SET code = :code" +
                " WHERE user_id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", userId);
        params.put("code", code);
        jdbcTemplate.update(sql, params);
    }
}
