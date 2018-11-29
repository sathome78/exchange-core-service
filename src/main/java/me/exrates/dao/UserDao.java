package me.exrates.dao;

import me.exrates.model.User;
import me.exrates.model.dto.TemporalToken;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.main.UserFile;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public interface UserDao {

    boolean setNickname(String newNickName, String userEmail);

    boolean create(User user);

    void createUserDoc(int userId, List<Path> paths);

    List<UserFile> findUserDoc(int userId);

    List<UserRole> getAllRoles();

    UserRole getUserRoleById(Integer id);

    boolean createAdminAuthoritiesForUser(Integer userId, UserRole role);

    boolean hasAdminAuthorities(Integer userId);

    boolean removeUserAuthorities(Integer userId);

    User findByEmail(String email);

    boolean ifNicknameIsUnique(String nickname);

    boolean ifEmailIsUnique(String email);

    String getIP(int userId);

    boolean setIP(int id, String ip);

    int getIdByEmail(String email);

    boolean addIPToLog(int userId, String ip);

    boolean update(UpdateUserDto user);

    User getUserById(int id);

    User getCommonReferralRoot();

    boolean createTemporalToken(TemporalToken token);

    TemporalToken verifyToken(String token);

    boolean deleteTemporalToken(TemporalToken token);

    boolean deleteTemporalToken(String tempToken);

    boolean deleteTemporalTokensOfTokentypeForUser(TemporalToken token);

    boolean delete(User user);

    String getPreferredLang(int userId);

    boolean setPreferredLang(int userId, Locale locale);

    String getPreferredLangByEmail(String email);

    boolean insertIp(String email, String ip);

    boolean setIpStateConfirmed(int userId, String ip);

    Long saveTemporaryPassword(Integer userId, String password, Integer tokenId);

    String getEmailById(Integer id);

    UserRole getUserRoleByEmail(String email);

    void savePollAsDoneByUser(String email);

    boolean updateLast2faNotifyDate(String email);

    LocalDate getLast2faNotifyDate(String email);

    String getPinByEmailAndEvent(String email, NotificationMessageEventEnum event);

    void updatePinByUserEmail(String userEmail, String pin, NotificationMessageEventEnum event);

    String get2faSecretByEmail(String email);

    boolean set2faSecretCode(String email);

    User getUserByTemporalToken(String token);

    List<TemporalToken> getAllTokens();

    String getAvatarPath(Integer userId);

    List<Integer> findFavouriteCurrencyPairsById(int id);

    boolean manageUserFavouriteCurrencyPair(int id, int currencyPairId, boolean delete);
}
