package me.exrates.dao;

import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.main.Commission;

public interface CommissionDao {
    Commission getCommission(OperationType operationTypeForAcceptor, UserRole userRoleFromDB);

    Commission getDefaultCommission(OperationType storno);
}
