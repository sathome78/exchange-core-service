package me.exrates.dao.userOperation;

import me.exrates.model.userOperation.UserOperationAuthorityOption;
import me.exrates.model.userOperation.enums.UserOperationAuthority;

import java.util.List;

public interface UserOperationDao {

    boolean getStatusAuthorityForUserByOperation(int userId, UserOperationAuthority userOperationAuthority);

}