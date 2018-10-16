package me.exrates.service.userOperation;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.userOperation.UserOperationDao;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class UserOperationServiceImpl implements UserOperationService {

    @Autowired
    private UserOperationDao userOperationDao;

    public boolean getStatusAuthorityForUserByOperation(int userId, UserOperationAuthority userOperationAuthority) {
        return userOperationDao.getStatusAuthorityForUserByOperation(userId, userOperationAuthority);
    }

}
