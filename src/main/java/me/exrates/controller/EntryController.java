package me.exrates.controller;

import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;
import me.exrates.exception.*;
import me.exrates.model.SessionParams;
import me.exrates.model.User;
import me.exrates.model.dto.*;
import me.exrates.model.enums.*;
import me.exrates.model.error.ErrorInfo;
import me.exrates.model.main.*;
import me.exrates.service.*;
import me.exrates.service.impl.UserSessionService;
import me.exrates.validator.RegisterFormValidation;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.ServiceUnavailableException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static me.exrates.util.BigDecimalProcessing.doAction;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Log4j2
@Controller
public class EntryController {
    private static final Logger LOGGER = LogManager.getLogger(EntryController.class);

    @Autowired
    MessageSource messageSource;
    @Value("${captcha.type}")
    String CAPTCHA_TYPE;
    @Value("${telegram.bot.url}")
    String TBOT_URL;
    @Value("${telegram_bot_name}")
    String TBOT_NAME;
    private
    @Value("${news.locationDir}")
    String newsLocationDir;
    @Autowired
    private LocaleResolver localeResolver;
    @Autowired
    private NewsService newsService;
    @Autowired
    private UserService userService;
    @Autowired
    private SessionParamsService sessionService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private NotificationsSettingsService settingsService;
    @Autowired
    private NotificatorsService notificatorService;
    @Autowired
    private ReferralService referralService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private RegisterFormValidation registerFormValidation;
    @Autowired
    private UserFilesService userFilesService;
    @Autowired
    private UserSessionService userSessionService;

    @RequestMapping(value = {"/dashboard"})
    public ModelAndView dashboard(
            @RequestParam(required = false) String qrLogin,
            @RequestParam(required = false) String sessionEnd,
            @RequestParam(required = false) String startupPage,
            @RequestParam(required = false) String startupSubPage,
            @RequestParam(required = false) String currencyPair,
            HttpServletRequest request, Principal principal) {
        ModelAndView model = new ModelAndView();
        String successNoty = null;
        String errorNoty = null;
        if (qrLogin != null) {
            successNoty = messageSource
                    .getMessage("dashboard.qrLogin.successful", null,
                            localeResolver.resolveLocale(request));
        }
        if (sessionEnd != null) {
            errorNoty = messageSource.getMessage("session.expire", null, localeResolver.resolveLocale(request));
        }
        if (StringUtils.isEmpty(successNoty)) {
            successNoty = (String) request.getSession().getAttribute("successNoty");
            request.getSession().removeAttribute("successNoty");
        }
        if (StringUtils.isEmpty(successNoty) && RequestContextUtils.getInputFlashMap(request) != null) {
            successNoty = (String) RequestContextUtils.getInputFlashMap(request).get("successNoty");
        }
        model.addObject("successNoty", successNoty);
        /**/
        if (StringUtils.isEmpty(errorNoty)) {
            errorNoty = (String) request.getSession().getAttribute("errorNoty");
            request.getSession().removeAttribute("errorNoty");
        }
        if (StringUtils.isEmpty(errorNoty) && RequestContextUtils.getInputFlashMap(request) != null) {
            errorNoty = (String) RequestContextUtils.getInputFlashMap(request).get("errorNoty");
        }
        /**/
        model.addObject("errorNoty", errorNoty);
        model.addObject("captchaType", CAPTCHA_TYPE);
        model.addObject("startupPage", startupPage == null ? "trading" : startupPage);
        model.addObject("startupSubPage", startupSubPage == null ? "" : startupSubPage);
        model.addObject("sessionId", request.getSession().getId());
        /*  model.addObject("startPoll", principal != null && !surveyService.checkPollIsDoneByUser(principal.getName()));
         */
        model.addObject("notify2fa", principal != null && userService.checkIsNotifyUserAbout2fa(principal.getName()));
        model.addObject("alwaysNotify2fa", principal != null && !userService.isLogin2faUsed(principal.getName()));
        model.setViewName("globalPages/dashboard");
        OrderCreateDto orderCreateDto = new OrderCreateDto();
        model.addObject(orderCreateDto);
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            int userStatus = user.getStatus().getStatus();
            model.addObject("userEmail", principal.getName());
            model.addObject("userStatus", userStatus);
            model.addObject("roleSettings", userRoleService.retrieveSettingsForRole(user.getRole().getRole()));
            model.addObject("referalPercents", referralService.findAllReferralLevels()
                    .stream()
                    .filter(p -> p.getPercent().compareTo(BigDecimal.ZERO) > 0)
                    .collect(toList()));
        }
        if (principal == null) {
            request.getSession().setAttribute("lastPageBeforeLogin", request.getRequestURI());
        }
        if (currencyPair != null) {
            currencyService.findPermitedCurrencyPairs(CurrencyPairType.MAIN).stream()
                    .filter(p -> p.getPairType() == CurrencyPairType.MAIN)
                    .filter(p -> p.getName().equals(currencyPair))
                    .limit(1)
                    .forEach(p -> model.addObject("preferedCurrencyPairName", currencyPair));
        }

        return model;
    }

    @RequestMapping(value = {"/ico_dashboard"})
    public ModelAndView icoDashboard(
            @RequestParam(required = false) String errorNoty,
            @RequestParam(required = false) String successNoty,
            @RequestParam(required = false) String startupPage,
            @RequestParam(required = false) String startupSubPage,
            @RequestParam(required = false) String currencyPair,
            HttpServletRequest request, Principal principal) {
        ModelAndView model = new ModelAndView();
        List<CurrencyPair> currencyPairs = currencyService.getAllCurrencyPairs(CurrencyPairType.ICO);
        if (currencyPairs.isEmpty()) {
            model.setViewName("redirect:/dashboard");
            return model;
        }
        if (StringUtils.isEmpty(successNoty)) {
            successNoty = (String) request.getSession().getAttribute("successNoty");
            request.getSession().removeAttribute("successNoty");
        }
        if (StringUtils.isEmpty(successNoty) && RequestContextUtils.getInputFlashMap(request) != null) {
            successNoty = (String) RequestContextUtils.getInputFlashMap(request).get("successNoty");
        }
        model.addObject("successNoty", successNoty);
        /**/
        if (StringUtils.isEmpty(errorNoty)) {
            errorNoty = (String) request.getSession().getAttribute("errorNoty");
            request.getSession().removeAttribute("errorNoty");
        }
        if (StringUtils.isEmpty(errorNoty) && RequestContextUtils.getInputFlashMap(request) != null) {
            errorNoty = (String) RequestContextUtils.getInputFlashMap(request).get("errorNoty");
        }
        /**/
        model.addObject("errorNoty", errorNoty);
        model.addObject("captchaType", CAPTCHA_TYPE);
        model.addObject("startupPage", startupPage == null ? "trading" : startupPage);
        model.addObject("startupSubPage", startupSubPage == null ? "" : startupSubPage);
        model.addObject("sessionId", request.getSession().getId());
        /*model.addObject("startPoll", principal != null && !surveyService.checkPollIsDoneByUser(principal.getName()));*/
        model.addObject("notify2fa", principal != null && userService.checkIsNotifyUserAbout2fa(principal.getName()));
        model.addObject("alwaysNotify2fa", principal != null && !userService.isLogin2faUsed(principal.getName()));
        model.setViewName("globalPages/ico_dashboard");
        OrderCreateDto orderCreateDto = new OrderCreateDto();
        model.addObject(orderCreateDto);
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            int userStatus = user.getStatus().getStatus();
            model.addObject("userEmail", principal.getName());
            model.addObject("userStatus", userStatus);
            model.addObject("roleSettings", userRoleService.retrieveSettingsForRole(user.getRole().getRole()));
            model.addObject("referalPercents", referralService.findAllReferralLevels()
                    .stream()
                    .filter(p -> p.getPercent().compareTo(BigDecimal.ZERO) > 0)
                    .collect(toList()));
        }
        if (principal == null) {
            request.getSession().setAttribute("lastPageBeforeLogin", request.getRequestURI());
        }
        if (currencyPair != null) {
            currencyService.findPermitedCurrencyPairs(CurrencyPairType.ICO).stream()
                    .filter(p -> p.getPairType() == CurrencyPairType.ICO)
                    .filter(p -> p.getName().equals(currencyPair))
                    .limit(1)
                    .forEach(p -> model.addObject("preferedCurrencyPairName", currencyPair));
        }

        return model;
    }


    @RequestMapping("/settings")
    public ModelAndView settings(Principal principal, @RequestParam(required = false) Integer tabIdx, @RequestParam(required = false) String msg,
                                 HttpServletRequest request) {
        final User user = userService.getUserById(userService.getIdByEmail(principal.getName()));
        final ModelAndView mav = new ModelAndView("globalPages/settings");
        final List<UserFile> userFile = userService.findUserDoc(user.getId());
        final Map<String, ?> map = RequestContextUtils.getInputFlashMap(request);
        List<NotificationOption> notificationOptions = notificationService.getNotificationOptionsByUser(user.getId());
        notificationOptions.forEach(option -> option.localize(messageSource, localeResolver.resolveLocale(request)));
        NotificationOptionsForm notificationOptionsForm = new NotificationOptionsForm();
        notificationOptionsForm.setOptions(notificationOptions);
        mav.addObject("user", user);
        mav.addObject("tabIdx", tabIdx);
        mav.addObject("sectionid", map != null && map.containsKey("sectionid") ? map.get("sectionid") : null);
        //mav.addObject("errorNoty", map != null ? map.get("msg") : msg);
        mav.addObject("userFiles", userFile);
        mav.addObject("notificationOptionsForm", notificationOptionsForm);
        mav.addObject("sessionSettings", sessionService.getByEmailOrDefault(user.getEmail()));
        mav.addObject("sessionLifeTimeTypes", sessionService.getAllByActive(true));
        mav.addObject("user2faOptions", settingsService.get2faOptionsForUser(user.getId()));
        mav.addObject("tBotName", TBOT_NAME);
        mav.addObject("tBotUrl", TBOT_URL);
        return mav;
    }

    /*todo move this method from admin controller*/
    @RequestMapping(value = "/settings/uploadFile", method = POST)
    public RedirectView uploadUserDocs(final @RequestParam("file") MultipartFile[] multipartFiles,
                                       RedirectAttributes redirectAttributes,
                                       final Principal principal,
                                       final Locale locale) {
        final RedirectView redirectView = new RedirectView("/settings");
        final User user = userService.getUserById(userService.getIdByEmail(principal.getName()));
        final List<MultipartFile> uploaded = userFilesService.reduceInvalidFiles(multipartFiles);
        redirectAttributes.addFlashAttribute("user", user);
        if (uploaded.isEmpty()) {
            redirectAttributes.addFlashAttribute("userFiles", userService.findUserDoc(user.getId()));
            redirectAttributes.addFlashAttribute("errorNoty", messageSource.getMessage("admin.errorUploadFiles", null, locale));
            return redirectView;
        }
        try {
            userFilesService.createUserFiles(user.getId(), uploaded);
        } catch (final IOException e) {
            log.error(e);
            redirectAttributes.addFlashAttribute("errorNoty", messageSource.getMessage("admin.internalError", null, locale));
            return redirectView;
        }
        redirectAttributes.addFlashAttribute("successNoty", messageSource.getMessage("admin.successUploadFiles", null, locale));
        redirectAttributes.addFlashAttribute("userFiles", userService.findUserDoc(user.getId()));
        redirectAttributes.addFlashAttribute("activeTabId", "files-upload-wrapper");
        return redirectView;
    }

    @ResponseBody
    @RequestMapping(value = "/settings/changePassword/submit", method = POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String submitsettingsPassword(@Valid @ModelAttribute ChangePasswordDto changePasswordDto, BindingResult result,
                                         Principal principal, HttpServletRequest request, HttpServletResponse response) {
        registerFormValidation.validateChangePassword(changePasswordDto, result, localeResolver.resolveLocale(request));
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        User userPrincipal = userService.findByEmail(principal.getName());
        Object message;
        if (result.hasErrors()) {
            response.setStatus(500);
            message = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(toList());
        } else {
            if (bCryptPasswordEncoder.matches(changePasswordDto.getPassword(), userPrincipal.getPassword())) {
                UpdateUserDto updateUserDto = new UpdateUserDto(userPrincipal.getId());
                updateUserDto.setPassword(changePasswordDto.getConfirmPassword());
                updateUserDto.setEmail(principal.getName());
                userService.update(updateUserDto, localeResolver.resolveLocale(request));
                message = messageSource.getMessage("user.settings.changePassword.successful", null, localeResolver.resolveLocale(request));
                userSessionService.invalidateUserSessionExceptSpecific(principal.getName(), RequestContextHolder.currentRequestAttributes().getSessionId());
            } else {
                response.setStatus(500);
                message = messageSource.getMessage("user.settings.changePassword.fail", null, localeResolver.resolveLocale(request));
            }
        }
        return new JSONObject() {{
            put("message", message);
        }}.toString();
    }

    @RequestMapping(value = "settings/changeNickname/submit", method = POST)
    public ModelAndView submitsettingsNickname(@RequestParam("nickname") String newNickName, BindingResult result,
                                               HttpServletRequest request, RedirectAttributes redirectAttributes, Principal principal) {
        registerFormValidation.validateNickname(newNickName, result, localeResolver.resolveLocale(request));
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorNoty", "Error. Nickname NOT changed.");
            redirectAttributes.addFlashAttribute("sectionid", "nickname-changing");
        } else {
            boolean userNicknameUpdated = userService.setNickname(newNickName, principal.getName());
            if (userNicknameUpdated) {
                redirectAttributes.addFlashAttribute("successNoty", "You have successfully updated nickname");
            } else {
                redirectAttributes.addFlashAttribute("errorNoty", "Error. Nickname NOT changed.");
            }
        }
        redirectAttributes.addFlashAttribute("activeTabId", "nickname-changing-wrapper");
        return new ModelAndView(new RedirectView("/settings"));
    }

    @RequestMapping(value = "/newIpConfirm")
    public ModelAndView verifyEmailForNewIp(@RequestParam("token") String token, HttpServletRequest req) {
        ModelAndView model = new ModelAndView();
        try {
            if (userService.verifyUserEmail(token) != 0) {
                req.getSession().setAttribute("successNoty", messageSource.getMessage("admin.newipproved", null, localeResolver.resolveLocale(req)));
            } else {
                req.getSession().setAttribute("errorNoty", messageSource.getMessage("admin.newipnotproved", null, localeResolver.resolveLocale(req)));
            }
            model.setViewName("redirect:/login");
        } catch (Exception e) {
            model.setViewName("DBError");
            e.printStackTrace();
        }
        return model;
    }

    @RequestMapping("/settings/notificationOptions/submit")
    public RedirectView submitNotificationOptions(@ModelAttribute NotificationOptionsForm notificationOptionsForm, RedirectAttributes redirectAttributes,
                                                  HttpServletRequest request, Principal principal) {
        notificationOptionsForm.getOptions().forEach(LOGGER::debug);
        int userId = userService.getIdByEmail(principal.getName());
        RedirectView redirectView = new RedirectView("/settings");
        List<NotificationOption> notificationOptions = notificationOptionsForm.getOptions().
                stream().
                map(option ->
                        {
                            option.setUserId(userId);
                            return option;
                        }
                ).
                collect(toList());
        ;
        //TODO uncomment after turning notifications on
        /*if (notificationOptions.stream().anyMatch(option -> !option.isSendEmail() && !option.isSendNotification())) {
            redirectAttributes.addFlashAttribute("msg", messageSource.getMessage("notifications.invalid", null,
                    localeResolver.resolveLocale(request)));
            return redirectView;

        }*/

        notificationService.updateUserNotifications(notificationOptions);
        redirectAttributes.addFlashAttribute("activeTabId", "notification-options-wrapper");
        return redirectView;
    }

    @RequestMapping("/settings/sessionOptions/submit")
    public RedirectView submitNotificationOptions(@ModelAttribute SessionParams sessionParams, RedirectAttributes redirectAttributes,
                                                  HttpServletRequest request, Principal principal) {
        RedirectView redirectView = new RedirectView("/settings");
        if (!sessionService.isSessionLifeTypeIdValid(sessionParams.getSessionLifeTypeId())) {
            sessionParams.setSessionLifeTypeId(SessionLifeTypeEnum.INACTIVE_COUNT_LIFETIME.getTypeId());
        }
        if (sessionService.isSessionTimeValid(sessionParams.getSessionTimeMinutes())) {
            try {
                sessionService.saveOrUpdate(sessionParams, principal.getName());
                sessionService.setSessionLifeParams(request);
                redirectAttributes.addFlashAttribute("successNoty", messageSource.getMessage("session.settings.success", null,
                        localeResolver.resolveLocale(request)));
            } catch (Exception e) {
                log.error("error", e);
                redirectAttributes.addFlashAttribute("msg", messageSource.getMessage("session.settings.invalid", null,
                        localeResolver.resolveLocale(request)));
            }
        } else {
            redirectAttributes.addFlashAttribute("msg", messageSource.getMessage("session.settings.time.invalid", null,
                    localeResolver.resolveLocale(request)));
        }
        redirectAttributes.addFlashAttribute("activeTabId", "session-options-wrapper");
        return redirectView;
    }

    @RequestMapping("/settings/2FaOptions/submit")
    public RedirectView submitNotificationOptions(RedirectAttributes redirectAttributes,
                                                  HttpServletRequest request, Principal principal) {
        RedirectView redirectView = new RedirectView("/settings");
        try {
            int userId = userService.getIdByEmail(principal.getName());
            Map<Integer, NotificationsUserSetting> settingsMap = settingsService.getSettingsMap(userId);
            settingsMap.forEach((k, v) -> {
                Integer notificatorId = Integer.parseInt(request.getParameter(k.toString()));
                if (notificatorId.equals(0)) {
                    notificatorId = null;
                }
                if (v == null) {
                    NotificationsUserSetting setting = NotificationsUserSetting.builder()
                            .userId(userId)
                            .notificatorId(notificatorId)
                            .notificationMessageEventEnum(NotificationMessageEventEnum.convert(k))
                            .build();
                    settingsService.createOrUpdate(setting);
                } else if (v.getNotificatorId() == null || !v.getNotificatorId().equals(notificatorId)) {
                    v.setNotificatorId(notificatorId);
                    settingsService.createOrUpdate(v);
                }
            });
            redirectAttributes.addFlashAttribute("successNoty", messageSource.getMessage("message.settings_successfully_saved", null,
                    localeResolver.resolveLocale(request)));
        } catch (Exception e) {
            log.error(e);
            redirectAttributes.addFlashAttribute("msg", messageSource.getMessage("message.error_saving_settings", null,
                    localeResolver.resolveLocale(request)));
            throw e;
        }
        redirectAttributes.addFlashAttribute("activeTabId", "2fa-options-wrapper");
        return redirectView;
    }

    @ResponseBody
    @RequestMapping("/settings/2FaOptions/getNotyPrice")
    public NotificatorTotalPriceDto getNotyPrice(@RequestParam int id, Principal principal) {
        Preconditions.checkArgument(id == NotificationTypeEnum.TELEGRAM.getCode());
        Subscribable subscribable = Preconditions.checkNotNull(notificatorService.getByNotificatorId(id));
        Object subscription = subscribable.getSubscription(userService.getIdByEmail(principal.getName()));
        UserRole role = userService.getUserRoleFromDB(principal.getName());
        NotificatorTotalPriceDto dto = notificatorService.getPrices(id, role.getRole());
        if (subscription != null && subscription instanceof TelegramSubscription) {
            if (!((TelegramSubscription) subscription).getSubscriptionState().isBeginState()) {
                throw new IllegalStateException();
            }
            dto.setCode(((TelegramSubscription) subscription).getCode());
        }
        return dto;
    }

    @ResponseBody
    @RequestMapping("/settings/2FaOptions/preconnect_sms")
    public String preconnectSms(@RequestParam String number, Principal principal, HttpServletRequest request) {
        number = number.replaceAll("\\+", "").replaceAll("\\-", "").replaceAll("\\.", "").replaceAll(" ", "");
        if (!NumberUtils.isDigits(number)) {
            throw new UnoperableNumberException();
        }
        Subscribable subscribable = notificatorService.getByNotificatorId(NotificationTypeEnum.SMS.getCode());
        int userId = userService.getIdByEmail(principal.getName());
        SmsSubscriptionDto subscriptionDto = SmsSubscriptionDto.builder()
                .userId(userId)
                .newContact(number)
                .build();
        return subscribable.prepareSubscription(subscriptionDto).toString();
    }

    @ResponseBody
    @RequestMapping("/settings/2FaOptions/confirm_connect_sms")
    public String connectSms(Principal principal) {
        Subscribable subscribable = notificatorService.getByNotificatorId(NotificationTypeEnum.SMS.getCode());
        subscribable.createSubscription(principal.getName());
        return "ok";
    }

    @ResponseBody
    @RequestMapping("/settings/2FaOptions/verify_connect_sms")
    public String verifyConnectSms(@RequestParam String code, Principal principal) {
        Subscribable subscribable = notificatorService.getByNotificatorId(NotificationTypeEnum.SMS.getCode());
        int userId = userService.getIdByEmail(principal.getName());
        SmsSubscriptionDto subscriptionDto = SmsSubscriptionDto.builder()
                .code(code)
                .userId(userId)
                .build();
        return subscribable.subscribe(subscriptionDto).toString();
    }

    @ResponseBody
    @RequestMapping("/settings/2FaOptions/connect_telegram")
    public String getNotyPrice(Principal principal) {
        Subscribable subscribable = notificatorService.getByNotificatorId(NotificationTypeEnum.TELEGRAM.getCode());
        return subscribable.createSubscription(principal.getName()).toString();
    }

    @ResponseBody
    @RequestMapping("/settings/2FaOptions/reconnect_telegram")
    public String reconnectTelegram(Principal principal) {
        Subscribable subscribable = notificatorService.getByNotificatorId(NotificationTypeEnum.TELEGRAM.getCode());
        return subscribable.reconnect(principal.getName()).toString();
    }

    @RequestMapping(value = "/settings/2FaOptions/google2fa", method = RequestMethod.POST)
    @ResponseBody
    public Generic2faResponseDto getGoogle2FA(Principal principal) throws UnsupportedEncodingException {
        return new Generic2faResponseDto(userService.generateQRUrl(principal.getName()));
    }

    @ResponseBody
    @RequestMapping("/settings/2FaOptions/verify_google2fa")
    public String verifyGoogleAuthenticatorConnect(@RequestParam String code, Principal principal) {
        if (principal != null) {
            userService.checkGoogle2faVerifyCode(code, principal.getName());
        }
        return "";
    }

    @ResponseBody
    @RequestMapping("/settings/2FaOptions/contact_info")
    public String getInfo(@RequestParam int id, Principal principal) {
        Subscribable subscribable = notificatorService.getByNotificatorId(id);
        Preconditions.checkNotNull(subscribable);
        NotificatorSubscription subscription = subscribable.getSubscription(userService.getIdByEmail(principal.getName()));
        Preconditions.checkState(subscription.isConnected());
        String contact = Preconditions.checkNotNull(subscription.getContactStr());
        int roleId = userService.getUserRoleFromSecurityContext().getRole();
        BigDecimal feePercent = notificatorService.getMessagePrice(id, roleId);
        BigDecimal price = doAction(doAction(subscription.getPrice(), feePercent, ActionType.MULTIPLY_PERCENT), subscription.getPrice(), ActionType.ADD);
        return new JSONObject() {{
            put("contact", contact);
            put("price", price);
        }}.toString();
    }

    /*skip resources: img, css, js*/
    @RequestMapping("/news/**/{newsVariant}/newstopic")
    public ModelAndView newsSingle(@PathVariable String newsVariant, HttpServletRequest request) {
        try {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("globalPages/newstopic");
            String path = request.getServletPath(); //   /news/2015/MAY/27/48/ru/newstopic.html
            int newsId = Integer.valueOf(path.split("\\/\\p{Alpha}+\\/{1}[^\\/]*$")[0].split("^.*[\\/]")[1]); // =>  /news/2015/MAY/27/48  => 48
//            String locale = path.split("\\/{1}[^\\/]*$")[0].split("^.*[\\/]")[1];
            News news = newsService.getNews(newsId, new Locale(newsVariant));
            if (news != null) {
                String newsContentPath = new StringBuilder()
                        .append(newsLocationDir)    //    /Users/Public/news/
                        .append(news.getResource()) //                      2015/MAY/27/
                        .append(newsId)             //                                  48
                        .append("/")                //                                     /
                        .append(newsVariant)   //      ru
                        //ignore locale from path and take it from fact locale .append(locale)   //                                                ru
                        .append("/newstopic.html")  //                                          /newstopic.html
                        .toString();                //  /Users/Public/news/2015/MAY/27/48/ru/newstopic.html
                LOGGER.debug("News content path: " + newsContentPath);
                try {
                    String newsContent = new String(Files.readAllBytes(Paths.get(newsContentPath)), "UTF-8"); //content of the newstopic.html
                    news.setContent(newsContent);
                    LOGGER.debug("News content: " + newsContent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                String newsContent = messageSource.getMessage("news.absent", null, localeResolver.resolveLocale(request));
                news = new News();
                news.setContent(newsContent);
                LOGGER.error("NEWS NOT FOUND");
            }
            modelAndView.addObject("captchaType", CAPTCHA_TYPE);
            modelAndView.addObject("news", news);
            return modelAndView;
        } catch (Exception e) {
            return null;
        }
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(NoFileForLoadingException.class)
    @ResponseBody
    public ErrorInfo NoFileForLoadingExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(FileLoadingException.class)
    @ResponseBody
    public ErrorInfo FileLoadingExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(NewsCreationException.class)
    @ResponseBody
    public ErrorInfo NewsCreationExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(ServiceUnavailableException.class)
    @ResponseBody
    public ErrorInfo SmsSubscribeExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception, messageSource.getMessage("message.service.unavialble", null, localeResolver.resolveLocale(req)));
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(IncorrectSmsPinException.class)
    @ResponseBody
    public ErrorInfo IncorrectSmsPinExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception, messageSource.getMessage("message.connectCode.wrong", null, localeResolver.resolveLocale(req)));
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(UnoperableNumberException.class)
    @ResponseBody
    public ErrorInfo SmsSubscribeUnoperableNumberExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception, messageSource.getMessage("message.numberUnoperable", null, localeResolver.resolveLocale(req)));
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(PaymentException.class)
    @ResponseBody
    public ErrorInfo msSubscribeMoneyExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception, messageSource.getMessage("message.notEnoughtUsd", null, localeResolver.resolveLocale(req)));
    }

}