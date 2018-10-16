package me.exrates.dao.impl;

import me.exrates.dao.UserRoleDao;
import me.exrates.model.dto.UserRoleSettings;
import me.exrates.model.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;

@Repository
public class UserRoleDaoImpl implements UserRoleDao {


    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public boolean isOrderAcceptionAllowedForUser(Integer userId) {
        String sql = "SELECT order_acception_same_role_only FROM USER_ROLE_SETTINGS where user_role_id = (SELECT roleid FROM USER WHERE id = :user_id)";
        return namedParameterJdbcTemplate.queryForObject(sql, Collections.singletonMap("user_id", userId), Boolean.class);
    }

    @Override
    public UserRoleSettings retrieveSettingsForRole(Integer roleId) {
        String sql = "SELECT user_role_id, order_acception_same_role_only, bot_acception_allowed, manual_change_allowed " +
                " FROM USER_ROLE_SETTINGS where user_role_id = :user_role_id";
        return namedParameterJdbcTemplate.queryForObject(sql, Collections.singletonMap("user_role_id", roleId), (rs, rowNum) -> {
            UserRoleSettings settings = new UserRoleSettings();
            settings.setUserRole(UserRole.convert(rs.getInt("user_role_id")));
            settings.setOrderAcceptionSameRoleOnly(rs.getBoolean("order_acception_same_role_only"));
            settings.setBotAcceptionAllowedOnly(rs.getBoolean("bot_acception_allowed"));
            settings.setManualChangeAllowed(rs.getBoolean("manual_change_allowed"));
            return settings;
        });
    }
}
