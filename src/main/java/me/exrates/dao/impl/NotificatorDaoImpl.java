package me.exrates.dao.impl;

import me.exrates.dao.NotificatorsDao;
import me.exrates.model.dto.Notificator;
import me.exrates.model.enums.NotificationPayTypeEnum;
import me.exrates.model.enums.NotificationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
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
    public int setEnable(int notificatorId, boolean enable) {
        String sql = "UPDATE 2FA_NOTIFICATOR SET enable = :enable " +
                " WHERE id = :notificator_id";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("enable", enable);
            put("notificator_id", notificatorId);
        }};
        return jdbcTemplate.update(sql, params);
    }

    @Override
    public List<Notificator> getAdminDtoByRole(int roleId) {
        String sql = "SELECT * FROM 2FA_NOTIFICATOR N " +
                " JOIN 2FA_NOTIFICATION_PRICE AS NP ON NP.notificator_id = N.id " +
                " WHERE NP.role_id = :role_id ";
        Map<String, Integer> params = Collections.singletonMap("role_id", roleId);
        return jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            Notificator notificator = notificatorRowMapper.mapRow(rs, rowNum);
            notificator.setMessagePrice(rs.getBigDecimal("message_price"));
            notificator.setSubscribePrice(rs.getBigDecimal("subscribe_price"));
            return notificator;
        });
    }

    @Override
    public List<Notificator> getAllNotificators() {
        String sql = "SELECT * FROM 2FA_NOTIFICATOR";
        return jdbcTemplate.query(sql, notificatorRowMapper);
    }
}
