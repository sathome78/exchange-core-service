package me.exrates.dao.impl;

import me.exrates.dao.UserAlertsDao;
import me.exrates.model.dto.AlertDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Maks on 13.12.2017.
 */
@Repository
public class UserAlertsDaoImpl implements UserAlertsDao {

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;

    private static RowMapper<AlertDto> getWalletsForOrderCancelDtoMapper = (rs, idx) -> {
        AlertDto alertDto = AlertDto
                .builder()
                .enabled(rs.getBoolean("enable"))
                .alertType(rs.getString("alert_type"))
                .build();
        Optional.ofNullable(rs.getTimestamp("launch_date"))
                .ifPresent(p->alertDto.setLaunchDateTime(p.toLocalDateTime()));
        Optional.ofNullable(rs.getTimestamp("time_of_start"))
                .ifPresent(p->alertDto.setEventStart(p.toLocalDateTime()));
        Optional.ofNullable(rs.getInt("length"))
                .ifPresent(alertDto::setLenghtOfWorks);
        return alertDto;
    };

    @Override
    public List<AlertDto> getAlerts(boolean getOnlyEnabled) {
        String sql = "SELECT SA.* FROM SERVICE_ALERTS SA ";
        if (getOnlyEnabled) {
            sql = sql.concat(" WHERE SA.enable = true ");
        }
        return jdbcTemplate.query(sql, new HashMap<>(), getWalletsForOrderCancelDtoMapper);
    }

    @Override
    public boolean updateAlert(AlertDto alertDto) {
        String sql = "UPDATE SERVICE_ALERTS SA SET SA.enable = :enable, " +
                " SA.launch_date = :launch_date, SA.time_of_start = :time_of_start, SA.length = :length " +
                " WHERE SA.alert_type = :alert_type ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("enable", alertDto.isEnabled());
            put("launch_date", alertDto.getLaunchDateTime());
            put("time_of_start", alertDto.getEventStart());
            put("length", alertDto.getLenghtOfWorks());
            put("alert_type", alertDto.getAlertType());
        }};
        return jdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public boolean setEnable(String alertType, boolean enable) {
        String sql = "UPDATE SERVICE_ALERTS SA SET SA.enable = :enable " +
                " WHERE SA.alert_type = :alert_type ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("enable", enable);
            put("alert_type", alertType);
        }};
        return jdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public AlertDto getAlert(String name) {
        String sql = "SELECT * FROM SERVICE_ALERTS SA WHERE SA.alert_type = :name";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("name", name);
        }};
        return jdbcTemplate.queryForObject(sql, params, getWalletsForOrderCancelDtoMapper);
    }


}
