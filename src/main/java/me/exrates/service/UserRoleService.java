package me.exrates.service;

import me.exrates.model.dto.UserRoleSettings;

public interface UserRoleService {
    boolean isOrderAcceptionAllowedForUser(Integer userId);

    UserRoleSettings retrieveSettingsForRole(Integer role);

}
