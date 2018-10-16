package me.exrates.dao;

import me.exrates.model.dto.UserRoleSettings;

public interface UserRoleDao {
    boolean isOrderAcceptionAllowedForUser(Integer userId);

    UserRoleSettings retrieveSettingsForRole(Integer roleId);
}
