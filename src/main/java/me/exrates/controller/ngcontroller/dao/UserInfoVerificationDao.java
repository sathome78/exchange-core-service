package me.exrates.controller.ngcontroller.dao;


import me.exrates.controller.ngcontroller.model.UserInfoVerificationDto;

public interface UserInfoVerificationDao {

    UserInfoVerificationDto save(UserInfoVerificationDto verificationDto);

    boolean delete(UserInfoVerificationDto verificationDto);

    UserInfoVerificationDto findByUserId(Integer userId);
}
