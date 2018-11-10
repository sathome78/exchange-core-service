package me.exrates.service.impl;

import me.exrates.dao.UserRoleDao;
import me.exrates.model.dto.UserRoleSettings;
import me.exrates.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRoleServiceImpl implements UserRoleService {
    @Autowired
    UserRoleDao userRoleDao;

    @Override
    @Transactional(readOnly = true)
    public boolean isOrderAcceptionAllowedForUser(Integer userId) {
        return userRoleDao.isOrderAcceptionAllowedForUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserRoleSettings retrieveSettingsForRole(Integer roleId) {
        return userRoleDao.retrieveSettingsForRole(roleId);
    }
}
