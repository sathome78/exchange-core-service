package me.exrates.dao;

import me.exrates.model.AdminAuthorityOption;
import me.exrates.model.User;
import me.exrates.model.dto.*;
import me.exrates.model.enums.InvoiceOperationDirection;
import me.exrates.model.enums.InvoiceOperationPermission;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.main.UserFile;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public interface UserDao {

    int getIdByNickname(String nickname);

    boolean setNickname(String newNickName, String userEmail);

    boolean create(User user);

    void createUserDoc(int userId, List<Path> paths);

    void setUserAvatar(int userId, String path);

    List<UserFile> findUserDoc(int userId);

    void deleteUserDoc(int docId);

    List<UserRole> getAllRoles();

    List<User> getUsersByRoles(List<UserRole> listRoles);

    UserRole getUserRoleById(Integer id);

    List<String> getUserRoleAndAuthorities(String email);

    List<AdminAuthorityOption> getAuthorityOptionsForUser(Integer userId);

    boolean createAdminAuthoritiesForUser(Integer userId, UserRole role);

    boolean createUserEntryDay(Integer userId, LocalDateTime entryDate);

    boolean hasAdminAuthorities(Integer userId);

    void updateAdminAuthorities(List<AdminAuthorityOption> options, Integer userId);

    boolean removeUserAuthorities(Integer userId);


    User findByEmail(String email);

    String getBriefInfo(int login);

    boolean ifNicknameIsUnique(String nickname);

    boolean ifPhoneIsUnique(int phone);

    boolean ifEmailIsUnique(String email);

    String getIP(int userId);

    boolean setIP(int id, String ip);

    int getIdByEmail(String email);

    boolean addIPToLog(int userId, String ip);

    boolean update(UpdateUserDto user);


    User findByNickname(String nickname);

    List<User> getAllUsers();

    User getUserById(int id);

    User getCommonReferralRoot();

    void updateCommonReferralRoot(int userId);

    UserRole getUserRoles(String email);

    boolean createTemporalToken(TemporalToken token);

    TemporalToken verifyToken(String token);

    boolean deleteTemporalToken(TemporalToken token);

    /**
     * Delete all tokens for user with concrete TokenType.
     * Uses in "Send again" in registration.
     *
     * @param token (TemporalToken)
     * @return boolean (false/true)
     */
    boolean deleteTemporalTokensOfTokentypeForUser(TemporalToken token);


    boolean updateUserStatus(User user);


    boolean delete(User user);

    String getPreferredLang(int userId);

    boolean setPreferredLang(int userId, Locale locale);

    String getPreferredLangByEmail(String email);

    boolean insertIp(String email, String ip);


    boolean setIpStateConfirmed(int userId, String ip);

    boolean setLastRegistrationDate(int userId, String ip);

    Long saveTemporaryPassword(Integer userId, String password, Integer tokenId);


    boolean updateUserPasswordFromTemporary(Long tempPassId);

    boolean deleteTemporaryPassword(Long id);

    boolean tempDeleteUser(int id);

    boolean tempDeleteUserWallets(int userId);

    List<UserSessionInfoDto> getUserSessionInfo(Set<String> emails);

    String getAvatarPath(Integer userId);


    void editUserComment(int id, String newComment, boolean sendMessage);

    boolean deleteUserComment(int id);

    Integer retrieveNicknameSearchLimit();

    List<String> findNicknamesByPart(String part, Integer limit);

    void setCurrencyPermissionsByUserId(Integer userId, List<UserCurrencyOperationPermissionDto> userCurrencyOperationPermissionDtoList);

    InvoiceOperationPermission getCurrencyPermissionsByUserIdAndCurrencyIdAndDirection(Integer userId, Integer currencyId, InvoiceOperationDirection invoiceOperationDirection);

    String getEmailById(Integer id);

    UserRole getUserRoleByEmail(String email);

    void savePollAsDoneByUser(String email);

    boolean checkPollIsDoneByUser(String email);

    boolean updateLast2faNotifyDate(String email);

    LocalDate getLast2faNotifyDate(String email);

    List<UserIpReportDto> getUserIpReportByRoleList(List<Integer> userRoleList);

    String getPinByEmailAndEvent(String email, NotificationMessageEventEnum event);

    void updatePinByUserEmail(String userEmail, String pin, NotificationMessageEventEnum event);

    Integer getNewRegisteredUserNumber(LocalDateTime startTime, LocalDateTime endTime);

    String get2faSecretByEmail(String email);

    boolean set2faSecretCode(String email);

    User getUserByTemporalToken(String token);

    List<TemporalToken> getAllTokens();

}
