package me.exrates.service;

import me.exrates.model.dto.UserRoleSettings;

public interface UserRoleService {
    boolean isOrderAcceptionAllowedForUser(int userId);

    UserRoleSettings retrieveSettingsForRole(int role);

}
