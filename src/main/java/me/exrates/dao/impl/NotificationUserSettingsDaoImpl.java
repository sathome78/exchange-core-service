package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.NotificationUserSettingsDao;
import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.enums.NotificationMessageEventEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maks on 02.10.2017.
 */
@Log4j2
@Repository
public class NotificationUserSettingsDaoImpl implements NotificationUserSettingsDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static RowMapper<NotificationsUserSetting> notificationsUserSettingRowMapper = (rs, idx) -> {
        NotificationsUserSetting setting = new NotificationsUserSetting();
        setting.setId(rs.getInt("id"));
        setting.setUserId(rs.getInt("user_id"));
        Object notificatorId = rs.getObject("notificator_id");
        setting.setNotificatorId(notificatorId == null ? null : Integer.valueOf(notificatorId.toString()));
        setting.setNotificationMessageEventEnum(NotificationMessageEventEnum.valueOf(rs.getString("event_name")));
        return setting;
    };


    @Override
    public NotificationsUserSetting getByUserAndEvent(int userId, NotificationMessageEventEnum event) {
        String sql = "SELECT UN.* FROM 2FA_USER_NOTIFICATION_MESSAGE_SETTINGS UN " +
                "WHERE UN.user_id = :user_id AND event_name = :event ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("user_id", userId);
            put("event", event.name());
        }};
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, params, notificationsUserSettingRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void update(NotificationsUserSetting setting) {
        final String sql = " UPDATE 2FA_USER_NOTIFICATION_MESSAGE_SETTINGS " +
                " SET notificator_id = :notificatorId " +
                " WHERE id = :id ";
        Map<String, Object> params = new HashMap<>();
        params.put("id", setting.getId());
        params.put("notificatorId", setting.getNotificatorId());
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public int create(NotificationsUserSetting setting) {
        final String sql = " INSERT INTO 2FA_USER_NOTIFICATION_MESSAGE_SETTINGS  " +
                " (user_id, notificator_id, event_name) " +
                " VALUES (:user_id, :notificator_id, :event_name) ";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", setting.getUserId())
                .addValue("notificator_id", setting.getNotificatorId())
                .addValue("event_name", setting.getNotificationMessageEventEnum().getCode());
        namedParameterJdbcTemplate.update(sql, params, keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public void delete(Integer userId) {
        final String sql = " DELETE FROM 2FA_USER_NOTIFICATION_MESSAGE_SETTINGS WHERE user_id = :id ";
        Map<String, Object> params = new HashMap<>();
        params.put("id", userId);
        namedParameterJdbcTemplate.update(sql, params);
    }
}
