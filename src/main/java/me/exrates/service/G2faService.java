package me.exrates.service;

import me.exrates.model.User;
import me.exrates.model.dto.Generic2faResponseDto;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface G2faService {

    void updateGoogleAuthenticatorSecretCodeForUser(Integer userId);

    boolean isGoogleAuthenticatorEnable(Integer userId);

    boolean isGoogleAuthenticatorEnable(String email);

    Generic2faResponseDto getGoogleAuthenticatorCodeNg(Integer userId);

    String generateQRUrl(String userEmail) throws UnsupportedEncodingException;

    String getGoogleAuthenticatorCode(Integer userId);

    boolean checkGoogle2faVerifyCode(String verificationCode, Integer userId);

    void setEnable2faGoogleAuth(Integer userId, Boolean connection);

    boolean submitGoogleSecret(User user, Map<String, String> body);

    boolean disableGoogleAuth(User user, Map<String, String> body);

    void sendGoogleAuthPinConfirm(User user, HttpServletRequest request);
}
