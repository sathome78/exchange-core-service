package me.exrates.service;

import me.exrates.exception.UnRegisteredUserDeleteException;
import me.exrates.model.User;
import me.exrates.model.dto.TemporalToken;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.main.UserFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public interface UserService {

    int getIdByEmail(String email);

    User findByEmail(String email);

    boolean create(User user, Locale locale, String source);

    boolean ifNicknameIsUnique(String nickname);

    boolean ifEmailIsUnique(String email);

    User getUserById(int id);

    boolean updateUserByAdmin(UpdateUserDto user);

    boolean update(UpdateUserDto user, boolean resetPassword, Locale locale);

    boolean update(UpdateUserDto user, Locale locale);

    boolean setPreferedLang(int userId, Locale locale);

    Locale getUserLocaleForMobile(String email);

    String getPreferedLangByEmail(String email);

    UserRole getUserRoleFromSecurityContext();

    String getEmailById(Integer id);

    UserRole getUserRoleFromDB(String email);

    UserRole getUserRoleFromDB(Integer userId);

    String getUserEmailFromSecurityContext();

    TemporalToken verifyUserEmailForForgetPassword(String token);

    User getUserByTemporalToken(String token);

    List<String> getLocalesList();

    String generateQRUrl(String userEmail) throws UnsupportedEncodingException;

    boolean checkIsNotifyUserAbout2fa(String name);

    boolean isLogin2faUsed(String name);

    List<UserFile> findUserDoc(int id);

    int verifyUserEmail(String token);

    boolean checkGoogle2faVerifyCode(String code, String name);

    boolean setNickname(String newNickName, String name);

    @Transactional(rollbackFor = Exception.class)
    void sendEmailWithToken(User user, TokenType tokenType, String tokenLink, String emailSubject, String emailText, Locale locale, String tempPass, String... params);

    void sendUnfamiliarIpNotificationEmail(User user, String emailSubject, String emailText, Locale locale);

    String getAvatarPath(Integer userId);

    User getCommonReferralRoot();

    String logIP(String email, String host);

    void sendEmailWithToken(User userForSend, TokenType registration, String s, String s1, String s2, Locale locale);

    boolean checkPin(String email, String pin, NotificationMessageEventEnum event);

    boolean isGlobal2FaActive();

    String getPreferedLang(Integer userId);

    @Transactional
    String updatePinForUserForEvent(String userEmail, NotificationMessageEventEnum event);

    boolean deleteExpiredToken(String token) throws UnRegisteredUserDeleteException;

    List<TemporalToken> getAllTokens();


    void createUserFile(int userId, List<Path> logicalPaths);

    List<Integer> getUserFavouriteCurrencyPairs(String principalEmail);

    boolean manageUserFavouriteCurrencyPair(String principalEmail, int currencyPairId, boolean toDelete);
}
