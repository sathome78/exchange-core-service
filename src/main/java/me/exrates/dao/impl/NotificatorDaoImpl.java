package me.exrates.dao.impl;

import me.exrates.dao.NotificatorsDao;
import me.exrates.model.dto.Notificator;
import me.exrates.model.enums.NotificationPayTypeEnum;
import me.exrates.model.enums.NotificationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class NotificatorDaoImpl implements NotificatorsDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private static RowMapper<Notificator> notificatorRowMapper = (rs, idx) -> {
        Notificator notificator = new Notificator();
        notificator.setId(rs.getInt("id"));
        notificator.setBeanName(rs.getString("bean_name"));
        notificator.setPayTypeEnum(NotificationPayTypeEnum.valueOf(rs.getString("pay_type")));
        notificator.setName(rs.getString("name"));
        notificator.setEnabled(rs.getBoolean("enable"));
        notificator.setNeedSubscribe(NotificationTypeEnum.convert(notificator.getId()).isNeedSubscribe());
        return notificator;
    };

    @Override
    public Notificator getById(int id) {
        String sql = "SELECT * FROM 2FA_NOTIFICATOR WHERE id = :id ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("id", id);
        }};
        return jdbcTemplate.queryForObject(sql, params, notificatorRowMapper);
    }

    @Override
    public List<Notificator> getAllNotificators() {
        String sql = "SELECT * FROM 2FA_NOTIFICATOR";
        return jdbcTemplate.query(sql, notificatorRowMapper);
    }
}
