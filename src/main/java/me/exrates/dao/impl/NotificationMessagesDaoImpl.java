package me.exrates.dao.impl;

import me.exrates.dao.NotificationMessagesDao;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.NotificationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationMessagesDaoImpl implements NotificationMessagesDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public String gerResourceString(NotificationMessageEventEnum event, NotificationTypeEnum typeEnum) {
        String sql = "SELECT NM.message FROM 2FA_NOTIFICATION_MESSAGES NM WHERE NM.event = :event AND NM.type = :type";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("event", event.name())
                .addValue("type", typeEnum.name());
        return jdbcTemplate.queryForObject(sql, params, String.class);
    }
}
