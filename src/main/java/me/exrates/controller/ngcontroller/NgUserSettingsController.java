package me.exrates.controller.ngcontroller;

import me.exrates.controller.ngcontroller.model.ExceptionDto;
import me.exrates.controller.ngcontroller.model.UserDocVerificationDto;
import me.exrates.controller.ngcontroller.model.UserInfoVerificationDto;
import me.exrates.controller.ngcontroller.model.enums.VerificationDocumentType;
import me.exrates.controller.ngcontroller.service.UserVerificationService;
import me.exrates.exception.UserNotFoundException;
import me.exrates.model.ColorScheme;
import me.exrates.model.SessionParams;
import me.exrates.model.User;
import me.exrates.model.dto.PageLayoutSettingsDto;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.enums.SessionLifeTypeEnum;
import me.exrates.model.main.NotificationOption;
import me.exrates.service.NotificationService;
import me.exrates.service.PageLayoutSettingsService;
import me.exrates.service.SessionParamsService;
import me.exrates.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.nio.file.NoSuchFileException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/info/private/v2/settings/",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
)
public class NgUserSettingsController {

    private static final Logger logger = LogManager.getLogger(NgUserSettingsController.class);

    private static final String NICKNAME = "nickname";
    private static final String SESSION_INTERVAL = "sessionInterval";
    private static final String EMAIL_NOTIFICATION = "notifications";
    private static final String COLOR_SCHEME = "color-schema";
    private static final String IS_COLOR_BLIND = "isLowColorEnabled";
    private static final String STATE = "STATE";

    private final UserService userService;
    private final NotificationService notificationService;
    private final SessionParamsService sessionService;
    private final PageLayoutSettingsService layoutSettingsService;
    private final UserVerificationService verificationService;

    @Value("${contacts.feedbackEmail}")
    String feedbackEmail;

    @Autowired
    public NgUserSettingsController(UserService userService,
                                    NotificationService notificationService,
                                    SessionParamsService sessionService,
                                    PageLayoutSettingsService layoutSettingsService,
                                    UserVerificationService verificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.sessionService = sessionService;
        this.layoutSettingsService = layoutSettingsService;
        this.verificationService = verificationService;
    }

    @PutMapping(value = "/updateMainPassword", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateMainPassword(@RequestBody Map<String, String> body) {
        String email = getPrincipalEmail();
        User user = userService.findByEmail(email);
        Locale locale = userService.getUserLocaleForMobile(email);
        String password = body.getOrDefault("password", "");
        if (password.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        user.setPassword(password);
        user.setConfirmPassword(password);

        //   registerFormValidation.validateResetPassword(user, result, locale);
        if (userService.update(getUpdateUserDto(user), locale)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping(value = NICKNAME)
    public ResponseEntity<Map<String, String>> getNickName() {
        User user = userService.findByEmail(getPrincipalEmail());
        String nickname = user.getNickname() == null ? "" : user.getNickname();
        return ResponseEntity.ok(Collections.singletonMap(NICKNAME, nickname));
    }

    @PutMapping(value = NICKNAME, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateNickName(@RequestBody Map<String, String> body) {
        User user = userService.findByEmail(getPrincipalEmail());
        if (body.containsKey(NICKNAME)) {
            user.setNickname(body.get(NICKNAME));
            if (userService.setNickname(user.getNickname(), user.getEmail())) {
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = SESSION_INTERVAL)
    public Integer getSessionPeriod() {
        SessionParams params = sessionService.getByEmailOrDefault(getPrincipalEmail());
        if (null == params) {
            return 0;
        }
        return params.getSessionTimeMinutes();
    }

    @PutMapping(value = SESSION_INTERVAL, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateSessionPeriod(@RequestBody Map<String, Integer> body) {
        try {
            int interval = body.get(SESSION_INTERVAL);
            SessionParams sessionParams = new SessionParams(interval, SessionLifeTypeEnum.INACTIVE_COUNT_LIFETIME.getTypeId());
            if (sessionService.isSessionTimeValid(sessionParams.getSessionTimeMinutes())) {
                sessionService.saveOrUpdate(sessionParams, getPrincipalEmail());
//                sessionService.setSessionLifeParams(request);
                //todo inform user to logout to implement params next time
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = EMAIL_NOTIFICATION)
    public Map<NotificationEvent, Boolean> getUserNotifications() {
        try {
            int userId = userService.getIdByEmail(getPrincipalEmail());
            return notificationService
                    .getNotificationOptionsByUser(userId)
                    .stream()
                    .collect(Collectors.toMap(NotificationOption::getEvent, NotificationOption::isSendEmail));
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    @PutMapping(value = EMAIL_NOTIFICATION, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateUserNotification(@RequestBody List<NotificationOption> options) {
        try {
            int userId = userService.getIdByEmail(getPrincipalEmail());
            notificationService.updateNotificationOptionsForUser(userId, options);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(IS_COLOR_BLIND)
    @ResponseBody
    public Boolean getUserColorDepth() {
        User user = userService.findByEmail(getPrincipalEmail());
        PageLayoutSettingsDto dto = this.layoutSettingsService.findByUser(user);
        return dto != null && dto.isLowColorEnabled();
    }

    @PutMapping(value = IS_COLOR_BLIND, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateUserColorDepth(@RequestBody Map<String, Boolean> params) {
        if (params.containsKey(STATE)) {
            User user = userService.findByEmail(getPrincipalEmail());
            this.layoutSettingsService.toggleLowColorMode(user, params.get(STATE));
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping(COLOR_SCHEME)
    @ResponseBody
    public ColorScheme getUserColorScheme() {
        User user = userService.findByEmail(getPrincipalEmail());
        return this.layoutSettingsService.getColorScheme(user);
    }

    @PutMapping(value = COLOR_SCHEME, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateUserColorScheme(@RequestBody Map<String, String> params) {
        if (params.containsKey("SCHEME")) {
            Integer userId = userService.getIdByEmail(getPrincipalEmail());
            PageLayoutSettingsDto settingsDto = PageLayoutSettingsDto
                    .builder()
                    .userId(userId)
                    .scheme(ColorScheme.of(params.get("SCHEME")))
                    .build();
            this.layoutSettingsService.save(settingsDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @PostMapping(value = "/docs")
    public ResponseEntity uploadUserVerification(@RequestBody @Valid UserInfoVerificationDto data) {
        logger.info("UserInfoVerificationDto - {}", data);
        int userId = userService.getIdByEmail(getPrincipalEmail());
        data.setUserId(userId);

        UserInfoVerificationDto attempt = verificationService.save(data);
        if (attempt != null) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/userFiles/docs/{type}")
    public ResponseEntity<Void> uploadUserVerificationDocs(@RequestBody Map<String, String> body,
                                                           @PathVariable("type") String type) {

        VerificationDocumentType documentType = VerificationDocumentType.of(type);
        int userId = userService.getIdByEmail(getPrincipalEmail());
        String encoded = body.getOrDefault("BASE_64", "");

        if (StringUtils.isEmpty(encoded)) {
            logger.info("uploadUserVerificationDocs() Error get data from file");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        UserDocVerificationDto data = new UserDocVerificationDto(userId, documentType, encoded);

        UserDocVerificationDto attempt = verificationService.save(data);
        if (attempt != null) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("currency_pair/favourites")
    @ResponseBody
    public List<Integer> getUserFavouriteCurrencyPairs() {
        return userService.getUserFavouriteCurrencyPairs(getPrincipalEmail());
    }

    @PutMapping("currency_pair/favourites")
    public ResponseEntity<Void> manegeUserFavouriteCurrencyPairs(@RequestBody  Map <String, String> params) {
        int currencyPairId;
        boolean toDelete;
         try {
            currencyPairId = Integer.parseInt(params.get("PAIR_ID"));
            toDelete = Boolean.valueOf(params.get("TO_DELETE"));
         } catch (Exception e) {
             logger.info("Failed to convert attributes as {}", e.getMessage());
             return ResponseEntity.badRequest().build();
         }
        boolean result = userService.manageUserFavouriteCurrencyPair(getPrincipalEmail(), currencyPairId, toDelete);
        if (result) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    private UpdateUserDto getUpdateUserDto(User user) {
        UpdateUserDto dto = new UpdateUserDto(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFinpassword(user.getFinpassword());
        dto.setPassword(user.getPassword());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        dto.setPhone(user.getPhone());
        return dto;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NoSuchFileException.class, UserNotFoundException.class})
    @ResponseBody
    public ResponseEntity<Object> NotFileFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<Object> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        return ResponseEntity.ok(new ExceptionDto(HttpStatus.BAD_REQUEST.toString(), e.getMessage()));
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    @ResponseBody
    public ResponseEntity<Object> AuthExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ResponseEntity<>("Not authorised", HttpStatus.UNAUTHORIZED);
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
