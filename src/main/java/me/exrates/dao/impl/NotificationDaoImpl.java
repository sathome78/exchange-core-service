package me.exrates.dao.impl;

import me.exrates.dao.NotificationDao;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.main.Notification;
import me.exrates.model.main.NotificationOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class NotificationDaoImpl implements NotificationDao {

    private final RowMapper<NotificationOption> notificationOptionRowMapper = (resultSet, row) -> {
        NotificationOption option = new NotificationOption();
        option.setEvent(NotificationEvent.convert(resultSet.getInt("notification_event_id")));
        option.setUserId(resultSet.getInt("user_id"));
        option.setSendNotification(resultSet.getBoolean("send_notification"));
        option.setSendEmail(resultSet.getBoolean("send_email"));
        return option;
    };

    @Autowired
//    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;


    @Override
    public boolean setRead(Long notificationId) {
        return false;
    }

    @Override
    public boolean remove(Long notificationId) {
        String sql = "DELETE FROM NOTIFICATION WHERE id = :id";
        Map<String, Long> params = Collections.singletonMap("id", notificationId);
        return jdbcTemplate.update(sql, params) == 1;
    }

    @Override
    public int setReadAllByUser(Integer userId) {
        String sql = "UPDATE NOTIFICATION SET is_read = 1 WHERE user_id = :user_id";
        Map<String, Integer> params = Collections.singletonMap("user_id", userId);
        return jdbcTemplate.update(sql, params);
    }

    @Override
    public int removeAllByUser(Integer userId) {
        String sql = "DELETE FROM NOTIFICATION WHERE user_id = :user_id";
        Map<String, Integer> params = Collections.singletonMap("user_id", userId);
        return jdbcTemplate.update(sql, params);
    }


    @Override
    public List<Notification> findAllByUser(Integer userId) {
        String sql = "SELECT id, user_id, title, message, creation_time, notification_event_id, is_read " +
                " FROM NOTIFICATION " +
                " WHERE user_id = :user_id";
        Map<String, Integer> params = Collections.singletonMap("user_id", userId);
        return jdbcTemplate.query(sql, params, (resultSet, row) -> {
            Notification notification = new Notification();
            notification.setId(resultSet.getLong("id"));
            notification.setReceiverUserId(resultSet.getInt("user_id"));
            notification.setTitle(resultSet.getString("title"));
            notification.setMessage(resultSet.getString("message"));
            notification.setCreationTime(resultSet.getTimestamp("creation_time").toLocalDateTime());
            notification.setCause(NotificationEvent.convert(resultSet.getInt("notification_event_id")));
            notification.setRead(resultSet.getBoolean("is_read"));
            return notification;
        });
    }

    @Override
    public void updateNotificationOptions(List<NotificationOption> options) {
        String sql = "UPDATE NOTIFICATION_OPTIONS SET /*send_notification = :send_notification,*/ send_email = :send_email " +
                " WHERE notification_event_id = :notification_event_id AND user_id = :user_id ";
        Map<String, Object>[] batchValues = options.stream().map(option -> {
            Map<String, Object> optionValues = new HashMap<String, Object>() {{
                put("notification_event_id", option.getEvent().getEventType());
                put("user_id", option.getUserId());
                /*put("send_notification", option.isSendNotification());*/
                put("send_email", option.isSendEmail());
            }};
            return optionValues;
        }).collect(Collectors.toList()).toArray(new Map[options.size()]);
        jdbcTemplate.batchUpdate(sql, batchValues);
    }

    @Override
    public List<NotificationOption> getNotificationOptionsByUser(int userId) {

        String sql = "SELECT notification_event_id, user_id, send_notification, send_email " +
                " FROM NOTIFICATION_OPTIONS " +
                " WHERE user_id = :user_id " +
                "ORDER BY notification_event_id DESC ";
        Map<String, Integer> params = Collections.singletonMap("user_id", userId);
        return jdbcTemplate.query(sql, params, notificationOptionRowMapper);

    }
}
