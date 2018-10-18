package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.UserDao;
import me.exrates.exception.IncorrectSmsPinException;
import me.exrates.exception.UnRegisteredUserDeleteException;
import me.exrates.model.Email;
import me.exrates.model.User;
import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.dto.TemporalToken;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.enums.*;
import me.exrates.model.main.UserFile;
import me.exrates.service.NotificationsSettingsService;
import me.exrates.service.ReferralService;
import me.exrates.service.SendMailService;
import me.exrates.service.UserService;
import me.exrates.service.token.TokenScheduler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Log4j2
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TokenScheduler tokenScheduler;

    @Autowired
    private ReferralService referralService;

    @Autowired
    private NotificationsSettingsService settingsService;

    /*this variable is set to use or not 2 factor authorization for all users*/
    private boolean global2FaActive = false;

    @Override
    public boolean isGlobal2FaActive() {
        return global2FaActive;
    }

    @Override
    public String getPreferedLang(Integer userId) {
        return userDao.getPreferredLang(userId);
    }

    @Override
    public String updatePinForUserForEvent(String userEmail, NotificationMessageEventEnum event) {
        String pin = String.valueOf(10000000 + new Random().nextInt(90000000));
        userDao.updatePinByUserEmail(userEmail, passwordEncoder.encode(pin), event);
        return pin;
    }

    @Override
    public boolean deleteExpiredToken(String token) throws UnRegisteredUserDeleteException {
        boolean result = false;
        TemporalToken temporalToken = userDao.verifyToken(token);
        result = userDao.deleteTemporalToken(temporalToken);
        if (temporalToken.getTokenType() == TokenType.REGISTRATION) {
            User user = userDao.getUserById(temporalToken.getUserId());
            if (user.getStatus() == UserStatus.REGISTERED) {
                LOGGER.debug(String.format("DELETING USER %s", user.getEmail()));
                referralService.updateReferralParentForChildren(user);
                result = userDao.delete(user);
                if (!result) {
                    throw new UnRegisteredUserDeleteException();
                }
            }
        }
        return result;
    }

    @Override
    public List<TemporalToken> getAllTokens() {
        return userDao.getAllTokens();
    }

    @Override
    public void createUserFile(int userId, List<Path> paths) {
        if (findUserDoc(userId).size() == USER_FILES_THRESHOLD) {
            throw new IllegalStateException("User (id:" + userId + ") can not have more than 3 docs on the server ");
        }
        userDao.createUserDoc(userId, paths);
    }

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
    public static String APP_NAME = "Exrates";

    private final int USER_FILES_THRESHOLD = 3;

    private final int USER_2FA_NOTIFY_DAYS = 6;

    private final Set<String> USER_ROLES = Stream.of(UserRole.values()).map(UserRole::name).collect(Collectors.toSet());
    private final UserRole ROLE_DEFAULT_COMMISSION = UserRole.USER;

    private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);

    private final static List<String> LOCALES_LIST = new ArrayList<String>() {{
        add("EN");
        add("RU");
        add("CN");
        add("ID");
        add("AR");
    }};

    @Override
    public List<String> getLocalesList() {
        return LOCALES_LIST;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean create(User user, Locale locale, String source) {
        Boolean flag = false;
        if (this.ifEmailIsUnique(user.getEmail())) {
            if (this.ifNicknameIsUnique(user.getNickname())) {
                if (userDao.create(user) && userDao.insertIp(user.getEmail(), user.getIpaddress())) {
                    int user_id = this.getIdByEmail(user.getEmail());
                    user.setId(user_id);
                    if (source != null && !source.isEmpty()) {
                        String view = "view=" + source;
                        sendEmailWithToken(user, TokenType.REGISTRATION, "/registrationConfirm", "emailsubmitregister.subject", "emailsubmitregister.text", locale, null, view);
                    } else {
                        sendEmailWithToken(user, TokenType.REGISTRATION, "/registrationConfirm", "emailsubmitregister.subject", "emailsubmitregister.text", locale);
                    }
                    flag = true;
                }
            }
        }
        return flag;
    }


    @Transactional(rollbackFor = Exception.class)
    public int verifyUserEmail(String token) {
        TemporalToken temporalToken = userDao.verifyToken(token);
        //deleting all tokens related with current through userId and tokenType
        return temporalToken != null ? deleteTokensAndUpdateUser(temporalToken) : 0;
    }

    private int deleteTokensAndUpdateUser(TemporalToken temporalToken) {
        if (userDao.deleteTemporalTokensOfTokentypeForUser(temporalToken)) {
            //deleting of appropriate jobs
            tokenScheduler.deleteJobsRelatedWithToken(temporalToken);
            /**/
            if (temporalToken.getTokenType() == TokenType.CONFIRM_NEW_IP) {
                if (!userDao.setIpStateConfirmed(temporalToken.getUserId(), temporalToken.getCheckIp())) {
                    return 0;
                }
            }
        }
        return temporalToken.getUserId();
    }

    /*
     * for checking if there are open tokens of concrete type for the user
     * */
    public int getIdByEmail(String email) {
        return userDao.getIdByEmail(email);
    }

    @Override
    public boolean setNickname(String newNickName, String userEmail) {
        return userDao.setNickname(newNickName, userEmail);
    }

    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public List<UserFile> findUserDoc(final int userId) {
        return userDao.findUserDoc(userId);
    }

    public boolean ifNicknameIsUnique(String nickname) {
        return userDao.ifNicknameIsUnique(nickname);
    }

    public boolean ifEmailIsUnique(String email) {
        return userDao.ifEmailIsUnique(email);
    }

    public String logIP(String email, String host) {
        int id = userDao.getIdByEmail(email);
        String userIP = userDao.getIP(id);
        if (userIP == null) {
            userDao.setIP(id, host);
        }
        userDao.addIPToLog(id, host);
        return userIP;
    }

    private String generateRegistrationToken() {
        return UUID.randomUUID().toString();

    }

    public User getUserById(int id) {
        return userDao.getUserById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserByAdmin(UpdateUserDto user) {
        boolean result = userDao.update(user);
        if (result) {
            boolean hasAdminAuthorities = userDao.hasAdminAuthorities(user.getId());
            if (user.getRole() == UserRole.USER && hasAdminAuthorities) {
                return userDao.removeUserAuthorities(user.getId());
            }
            if (!hasAdminAuthorities && user.getRole() != null &&
                    user.getRole() != UserRole.USER && user.getRole() != UserRole.ROLE_CHANGE_PASSWORD) {
                return userDao.createAdminAuthoritiesForUser(user.getId(), user.getRole());
            }
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean update(UpdateUserDto user, boolean resetPassword, Locale locale) {
        boolean changePassword = user.getPassword() != null && !user.getPassword().isEmpty();
        boolean changeFinPassword = user.getFinpassword() != null && !user.getFinpassword().isEmpty();

        if (userDao.update(user)) {
            User u = new User();
            u.setId(user.getId());
            u.setEmail(user.getEmail());
            if (changePassword) {
                sendUnfamiliarIpNotificationEmail(u, "admin.changePasswordTitle", "user.settings.changePassword.successful", locale);
            } else if (changeFinPassword) {
                sendEmailWithToken(u, TokenType.CHANGE_FIN_PASSWORD, "/changeFinPasswordConfirm", "emailsubmitChangeFinPassword.subject", "emailsubmitChangeFinPassword.text", locale);
            } else if (resetPassword) {
                sendEmailWithToken(u, TokenType.CHANGE_PASSWORD, "/resetPasswordConfirm", "emailsubmitResetPassword.subject", "emailsubmitResetPassword.text", locale);
            }
        }
        return true;
    }

    @Override
    public boolean update(UpdateUserDto user, Locale locale) {
        return update(user, false, locale);
    }


    @Override
    public void sendEmailWithToken(User user, TokenType tokenType, String tokenLink, String emailSubject, String emailText, Locale locale) {
        sendEmailWithToken(user, tokenType, tokenLink, emailSubject, emailText, locale, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendEmailWithToken(User user, TokenType tokenType, String tokenLink, String emailSubject, String emailText, Locale locale, String tempPass, String... params) {
        TemporalToken token = new TemporalToken();
        token.setUserId(user.getId());
        token.setValue(generateRegistrationToken());
        token.setTokenType(tokenType);
        token.setCheckIp(user.getIpaddress());
        token.setAlreadyUsed(false);

        createTemporalToken(token);
        String tempPassId = "";
        if (tempPass != null) {
            tempPassId = "&tempId=" + userDao.saveTemporaryPassword(user.getId(), tempPass, userDao.verifyToken(token.getValue()).getId());
        }

        Email email = new Email();
        StringBuilder confirmationUrl = new StringBuilder(tokenLink + "?token=" + token.getValue() + tempPassId);
        if (tokenLink.equals("/resetPasswordConfirm")) {
            confirmationUrl.append("&email=").append(user.getEmail());
        }
        String rootUrl = "";
        if (!confirmationUrl.toString().contains("//")) {
            rootUrl = request.getScheme() + "://" + request.getServerName() +
                    ":" + request.getServerPort();
        }
        if (params != null) {
            for (String patram : params) {
                confirmationUrl.append("&").append(patram);
            }
        }
        email.setMessage(
                messageSource.getMessage(emailText, null, locale) +
                        " <a href='" +
                        rootUrl +
                        confirmationUrl.toString() +
                        "'>" + messageSource.getMessage("admin.ref", null, locale) + "</a>"
        );
        email.setSubject(messageSource.getMessage(emailSubject, null, locale));

        email.setTo(user.getEmail());
        if (tokenType.equals(TokenType.REGISTRATION)
                || tokenType.equals(TokenType.CHANGE_PASSWORD)
                || tokenType.equals(TokenType.CHANGE_FIN_PASSWORD)) {
            sendMailService.sendMailMandrill(email);
        } else {
            sendMailService.sendMail(email);
        }
    }

    @Override
    public void sendUnfamiliarIpNotificationEmail(User user, String emailSubject, String emailText, Locale locale) {
        Email email = new Email();
        email.setTo(user.getEmail());
        email.setMessage(messageSource.getMessage(emailText, new Object[]{user.getIpaddress()}, locale));
        email.setSubject(messageSource.getMessage(emailSubject, null, locale));
        sendMailService.sendInfoMail(email);
    }

    public boolean createTemporalToken(TemporalToken token) {
        log.info("Token is " + token);
        boolean result = userDao.createTemporalToken(token);
        if (result) {
            log.info("Token succesfully saved");
            tokenScheduler.initTrigers();
        }
        return result;
    }

    @Override
    public User getCommonReferralRoot() {
        try {
            return userDao.getCommonReferralRoot();
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    public boolean setPreferedLang(int userId, Locale locale) {
        return userDao.setPreferredLang(userId, locale);
    }

    @PostConstruct
    private void initTokenTriggers() {
        tokenScheduler.initTrigers();
    }


    @Override
    public Locale getUserLocaleForMobile(String email) {
        String lang = getPreferedLangByEmail(email);
        //adaptation for locales available in mobile app
        if (!("ru".equalsIgnoreCase(lang) || "en".equalsIgnoreCase(lang))) {
            lang = "en";
        }

        return new Locale(lang);
    }

    @Override
    public String getPreferedLangByEmail(String email) {
        return userDao.getPreferredLangByEmail(email);
    }

    @Override
    public UserRole getUserRoleFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String grantedAuthority = authentication.getAuthorities().
                stream().map(GrantedAuthority::getAuthority)
                .filter(USER_ROLES::contains)
                .findFirst().orElse(ROLE_DEFAULT_COMMISSION.name());
        LOGGER.debug("Granted authority: " + grantedAuthority);
        return UserRole.valueOf(grantedAuthority);
    }

    @Override
    @Transactional(readOnly = true)
    public String getEmailById(Integer id) {
        return userDao.getEmailById(id);
    }

    @Override
    public UserRole getUserRoleFromDB(String email) {
        return userDao.getUserRoleByEmail(email);
    }

    @Override
    @Transactional
    public UserRole getUserRoleFromDB(Integer userId) {
        return userDao.getUserRoleById(userId);
    }

    @Override
    public boolean checkPin(String email, String pin, NotificationMessageEventEnum event) {
        int userId = getIdByEmail(email);
        NotificationsUserSetting setting = settingsService.getByUserAndEvent(userId, event);
        if ((setting == null || setting.getNotificatorId() == null) && !event.isCanBeDisabled()) {
            setting = NotificationsUserSetting.builder()
                    .notificatorId(NotificationTypeEnum.EMAIL.getCode())
                    .userId(userId)
                    .notificationMessageEventEnum(event)
                    .build();
        }

        if (setting.getNotificatorId() == 4) {
            return checkGoogle2faVerifyCode(pin, email);
        }

        return passwordEncoder.matches(pin, getPinForEvent(email, event));
    }

    private String getPinForEvent(String email, NotificationMessageEventEnum event) {
        return userDao.getPinByEmailAndEvent(email, event);
    }

    @Override
    public boolean isLogin2faUsed(String email) {
        NotificationsUserSetting setting = settingsService.getByUserAndEvent(getIdByEmail(email), NotificationMessageEventEnum.LOGIN);
        return setting != null && setting.getNotificatorId() != null;
    }

    @Override
    @Transactional
    public String generateQRUrl(String userEmail) throws UnsupportedEncodingException {
        String secret2faCode = userDao.get2faSecretByEmail(userEmail);
        if (secret2faCode == null || secret2faCode.isEmpty()) {
            userDao.set2faSecretCode(userEmail);
            secret2faCode = userDao.get2faSecretByEmail(userEmail);
        }
        return QR_PREFIX + URLEncoder.encode(String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", APP_NAME, userEmail, secret2faCode, APP_NAME), "UTF-8");
    }

    @Override
    public boolean checkIsNotifyUserAbout2fa(String email) {
        LocalDate lastNotyDate = userDao.getLast2faNotifyDate(email);
        boolean res = !isLogin2faUsed(email) &&
                (lastNotyDate == null || lastNotyDate.plusDays(USER_2FA_NOTIFY_DAYS).isBefore(LocalDate.now()));
        if (res) {
            userDao.updateLast2faNotifyDate(email);
        }
        return res;
    }

    private boolean isValidLong(String code) {
        try {
            Long.parseLong(code);
        } catch (final NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    public String getUserEmailFromSecurityContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new AuthenticationNotAvailableException();
        }
        return auth.getName();
    }

    @Override
    public boolean checkGoogle2faVerifyCode(String verificationCode, String userEmail) {
        String google2faSecret = userDao.get2faSecretByEmail(userEmail);
        final Totp totp = new Totp(google2faSecret);
        if (!isValidLong(verificationCode) || !totp.verify(verificationCode)) {
            System.out.println("WTF?!@!!!!");
            throw new IncorrectSmsPinException();
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public TemporalToken verifyUserEmailForForgetPassword(String token) {
        return userDao.verifyToken(token);
    }

    public User getUserByTemporalToken(String token) {
        return userDao.getUserByTemporalToken(token);
    }

}
