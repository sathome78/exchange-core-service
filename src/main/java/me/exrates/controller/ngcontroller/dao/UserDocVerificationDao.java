package me.exrates.controller.ngcontroller.dao;

import me.exrates.controller.ngcontroller.model.UserDocVerificationDto;
import me.exrates.controller.ngcontroller.model.enums.VerificationDocumentType;
import me.exrates.model.User;

import java.util.List;

public interface UserDocVerificationDao {

    UserDocVerificationDto save(UserDocVerificationDto verificationDto);

    boolean delete(UserDocVerificationDto verificationDto);

    UserDocVerificationDto findByUserIdAndDocumentType(Integer userId, VerificationDocumentType documentType);

    List<UserDocVerificationDto> findAllByUser(User user);

}
