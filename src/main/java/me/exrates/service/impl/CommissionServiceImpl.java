package me.exrates.service.impl;

import me.exrates.dao.CommissionDao;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.main.Commission;
import me.exrates.service.CommissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommissionServiceImpl implements CommissionService {

    @Autowired
    CommissionDao commissionDao;

    @Override
    public Commission findCommissionByTypeAndRole(OperationType operationType, UserRole userRole) {
        return commissionDao.getCommission(operationType, userRole);
    }

    @Override
    public Commission getDefaultCommission(OperationType operationType) {
        return commissionDao.getDefaultCommission(operationType);
    }

}
