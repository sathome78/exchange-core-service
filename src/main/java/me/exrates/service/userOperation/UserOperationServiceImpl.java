package me.exrates.service.userOperation;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.UserDao;
import me.exrates.dao.userOperation.UserOperationDao;
import me.exrates.exception.ForbiddenOperationException;
import me.exrates.model.enums.UserRole;
import me.exrates.model.userOperation.UserOperationAuthorityOption;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Log4j2
@Service
public class UserOperationServiceImpl implements UserOperationService {

  @Autowired
  private UserOperationDao userOperationDao;

  public boolean getStatusAuthorityForUserByOperation(int userId, UserOperationAuthority userOperationAuthority) {
      return userOperationDao.getStatusAuthorityForUserByOperation(userId, userOperationAuthority);
  }

}
