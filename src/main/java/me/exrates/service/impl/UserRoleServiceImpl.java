package me.exrates.service.impl;

import me.exrates.model.dto.UserRoleSettings;
import me.exrates.service.UserRoleService;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl implements UserRoleService {
    @Override
    public boolean isOrderAcceptionAllowedForUser(int userId) {
        return false;
    }

    @Override
    public UserRoleSettings retrieveSettingsForRole(int role) {
        return null;
    }
}
