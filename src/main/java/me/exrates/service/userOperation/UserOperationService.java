package me.exrates.service.userOperation;

import me.exrates.model.userOperation.enums.UserOperationAuthority;

public interface UserOperationService {

    boolean getStatusAuthorityForUserByOperation(int userId, UserOperationAuthority userOperationAuthority);

}
