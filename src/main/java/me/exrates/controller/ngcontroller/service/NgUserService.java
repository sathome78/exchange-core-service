package me.exrates.controller.ngcontroller.service;


import me.exrates.controller.ngcontroller.model.PasswordCreateDto;
import me.exrates.model.AuthTokenDto;
import me.exrates.model.dto.UserEmailDto;

import javax.servlet.http.HttpServletRequest;

public interface NgUserService {

    boolean registerUser(UserEmailDto userEmailDto, HttpServletRequest request);

    AuthTokenDto createPassword(PasswordCreateDto passwordCreateDto, HttpServletRequest request);

    boolean recoveryPassword(UserEmailDto userEmailDto, HttpServletRequest request);

}
