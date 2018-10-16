package me.exrates.service;

import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.main.Commission;

import javax.swing.*;

public interface CommissionService {
    Commission findCommissionByTypeAndRole(OperationType sell, UserRole userRole);

    Commission getDefaultCommission(OperationType referral);
}
