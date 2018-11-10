package me.exrates.dao.userOperation;

import me.exrates.model.userOperation.enums.UserOperationAuthority;

public interface UserOperationDao {

    boolean getStatusAuthorityForUserByOperation(int userId, UserOperationAuthority userOperationAuthority);

}