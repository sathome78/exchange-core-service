package me.exrates.controller;

import com.captcha.botdetect.web.servlet.Captcha;
import me.exrates.exception.*;
import me.exrates.exception.security.exception.IncorrectPinException;
import me.exrates.exception.security.exception.PinCodeCheckNeedException;
import me.exrates.exception.security.exception.UnconfirmedUserException;
import me.exrates.model.User;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.dto.UserEmailDto;
import me.exrates.model.enums.*;
import me.exrates.model.error.ErrorInfo;
import me.exrates.model.form.FeedbackMessageForm;
import me.exrates.model.vo.openApiDoc.OpenApiMethodDoc;
import me.exrates.model.vo.openApiDoc.OpenApiMethodGroup;
import me.exrates.service.*;
import me.exrates.util.IpUtils;
import me.exrates.util.geetest.GeetestLib;
import me.exrates.validator.FeedbackMessageFormValidator;
import me.exrates.validator.RegisterFormValidation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonMap;

@Controller

public class MainController {

    private static final Logger logger = LogManager.getLogger(MainController.class);
    @Value("${captcha.type}")
    String CAPTCHA_TYPE;
    private
    @Value("${contacts.telephone}")
    String telephone;
    private
    @Value("${contacts.email}")
    String email;
    @Value("${contacts.feedbackEmail}")
    String feedbackEmail;
    @Autowired
    private UserService userService;
    @Autowired
    private RegisterFormValidation registerFormValidation;
    @Autowired
    private FeedbackMessageFormValidator messageFormValidator;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private LocaleResolver localeResolver;
    @Autowired
    private VerifyReCaptchaSec verifyReCaptchaSec;
    @Autowired
    private ReferralService referralService;
    @Autowired
    private SendMailService sendMailService;
    @Autowired
    private SecureService secureService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private GeetestLib geetest;

    @RequestMapping(value = "57163a9b3d1eafe27b8b456a.txt", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getFile() {
        final ClassLoader classLoader = getClass().getClassLoader();
        final File file = new File(classLoader.getResource("public/57163a9b3d1eafe27b8b456a.txt").getFile());
        return new FileSystemResource(file);
    }

    @RequestMapping("/403")
    public String error403() {
        return "403";
    }

    /**
     * Register user on referral link (redirect to dashboard, call pop-up with registration)
     *
     * @param refReference
     * @param attr
     * @return ModalAndView (dashboard), referral link (if this exists), user object with parent email
     */
    @RequestMapping("/register")
    public ModelAndView registerUser(@RequestParam(value = "ref", required = false) String refReference, RedirectAttributes attr) {

        User refferalRoot = userService.getCommonReferralRoot();
        String parentEmail = "";

        if (!Objects.isNull(refReference)) {
            final Optional<Integer> parentId = referralService.reduceReferralRef(refReference);
            if (parentId.isPresent()) {
                parentEmail = userService.getUserById(parentId.get()).getEmail();
            }
        } else if (refferalRoot != null) {
            parentEmail = refferalRoot.getEmail();
        }

        logger.info("*** Used referral link with reference (" + refReference + ") && Parent email: " + parentEmail);

        attr.addFlashAttribute("refferalLink", refReference);
        attr.addFlashAttribute("parentEmail", parentEmail);

        return new ModelAndView(new RedirectView("/dashboard"));
    }

    @RequestMapping("/generateReferral")
    public
    @ResponseBody
    Map<String, String> generateReferral(final Principal principal) {
        if (principal == null) return null;
        return singletonMap("referral", referralService.generateReferral(principal.getName()));
    }


    @RequestMapping(value = "/createUser", method = RequestMethod.POST)
    public ResponseEntity createNewUser(@ModelAttribute("user") UserEmailDto userEmailDto, @RequestParam(required = false) String source,
                                        BindingResult result, HttpServletRequest request) {
        String challenge = request.getParameter(GeetestLib.fn_geetest_challenge);
        String validate = request.getParameter(GeetestLib.fn_geetest_validate);
        String seccode = request.getParameter(GeetestLib.fn_geetest_seccode);
        User user = new User();
        user.setEmail(userEmailDto.getEmail());
        user.setParentEmail(userEmailDto.getParentEmail());
        int gt_server_status_code = (Integer) request.getSession().getAttribute(geetest.gtServerStatusSessionKey);
        String userid = (String) request.getSession().getAttribute("userid");

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", userid);

        int gtResult = 0;
        if (gt_server_status_code == 1) {
            gtResult = geetest.enhencedValidateRequest(challenge, validate, seccode, param);
            logger.info(gtResult);
        } else {
            logger.error("failback:use your own server captcha validate");
            gtResult = geetest.failbackValidateRequest(challenge, validate, seccode);
            logger.error(gtResult);
        }

        if (gtResult == 1) {
            registerFormValidation.validate(null, user.getEmail(), null, result, localeResolver.resolveLocale(request));
            user.setPhone("");
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result);
            } else {
                boolean flag = false;
                try {
                    String ip = IpUtils.getClientIpAddress(request, 100);
                    if (ip == null) {
                        ip = request.getRemoteHost();
                    }
                    user.setIpaddress(ip);
                    if (userService.create(user, localeResolver.resolveLocale(request), source)) {
                        flag = true;
                        logger.info("User registered with parameters = " + user.toString());
                    } else {
                        throw new NotCreateUserException("Error while user creation");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("User can't be registered with parameters = " + user.toString() + "  " + e.getMessage());
                }
                if (flag) {
                    final int child = userService.getIdByEmail(user.getEmail());
                    final int parent = userService.getIdByEmail(user.getParentEmail());
                    if (child > 0 && parent > 0) {
                        referralService.bindChildAndParent(child, parent);
                        logger.info("*** Referal graph | Child: " + user.getEmail() + " && Parent: " + user.getParentEmail());
                    }

                    String successNoty = null;
                    try {
                        successNoty = URLEncoder.encode(messageSource.getMessage("register.sendletter", null,
                                localeResolver.resolveLocale(request)), "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    Map<String, Object> body = new HashMap<>();
                    body.put("result", successNoty);
                    body.put("user", user);
                    return ResponseEntity.ok(body);

                } else {
                    throw new NotCreateUserException("DBError");
                }
            }
        } else {
            //TODO
            throw new RuntimeException("Geetest error");
        }
    }

    @RequestMapping(value = "/createPassword", method = RequestMethod.GET)
    public ModelAndView createPassword(@ModelAttribute User user, @RequestParam(required = false) String view) {
        ModelAndView mav = new ModelAndView("fragments/createPassword");
        mav.addObject("view", view);
        mav.addObject("user", user);
        return mav;
    }

    @RequestMapping(value = "/createPasswordConfirm", method = RequestMethod.POST)
    public ModelAndView createPassword(@ModelAttribute User user,
                                       @RequestParam(required = false) String view,
                                       BindingResult result,
                                       HttpServletRequest request,
                                       RedirectAttributes attr) {
        registerFormValidation.validate(null, null, user.getPassword(), result, localeResolver.resolveLocale(request));
        if (result.hasErrors()) {
            //TODO
            throw new PasswordCreationException("Error while creating password.");
        } else {
            User userUpdate = userService.findByEmail(user.getEmail());
            UpdateUserDto updateUserDto = new UpdateUserDto(userUpdate.getId());
            updateUserDto.setPassword(user.getPassword());
            updateUserDto.setRole(UserRole.USER);
            updateUserDto.setStatus(UserStatus.ACTIVE);
            userService.updateUserByAdmin(updateUserDto);
            Collection<GrantedAuthority> authList = new ArrayList<>(userDetailsService.loadUserByUsername(user.getEmail()).getAuthorities());
            org.springframework.security.core.userdetails.User userSpring = new org.springframework.security.core.userdetails.User(
                    user.getEmail(), updateUserDto.getPassword(), false, false, false, false, authList);
            Authentication auth = new UsernamePasswordAuthenticationToken(userSpring, null, authList);
            SecurityContextHolder.getContext().setAuthentication(auth);

            attr.addFlashAttribute("successNoty", messageSource.getMessage("register.successfullyproved", null, localeResolver.resolveLocale(request)));

            if (view != null && view.equals("ico_dashboard")) {
                return new ModelAndView("redirect:/ico_dashboard");
            }

            return new ModelAndView("redirect:/dashboard");
        }
    }

    @RequestMapping(value = "/registrationConfirm")
    public ModelAndView verifyEmail(HttpServletRequest request,
                                    @RequestParam("token") String token,
                                    @RequestParam(required = false) String view,
                                    RedirectAttributes attr) {
        ModelAndView model = new ModelAndView();
        try {
            int userId = userService.verifyUserEmail(token);
            if (userId != 0) {
                User user = userService.getUserById(userId);
                attr.addFlashAttribute("successConfirm", messageSource.getMessage("register.successfullyproved", null, localeResolver.resolveLocale(request)));
                attr.addFlashAttribute("user", user);

                user.setRole(UserRole.ROLE_CHANGE_PASSWORD);
                user.setStatus(UserStatus.REGISTERED);
                user.setPassword(null);

                if (view != null) {
                    model.addObject("view", view);
                    model.setViewName("redirect:/createPassword");
                }
            } else {
                attr.addFlashAttribute("errorNoty", messageSource.getMessage("register.unsuccessfullyproved", null, localeResolver.resolveLocale(request)));
                model.setViewName("redirect:/dashboard");
            }
        } catch (Exception e) {
            model.setViewName("DBError");
            e.printStackTrace();
            logger.error("Error while verifing user registration email  " + e.getLocalizedMessage());
        }
        return model;
    }

    @RequestMapping("/personalpage")
    public ModelAndView gotoPersonalPage(@ModelAttribute User user, Principal principal) {
        String host = request.getRemoteHost();
        String email = principal.getName();
        String userIP = userService.logIP(email, host);
        return new ModelAndView("personalpage", "userIP", userIP);
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(HttpSession httpSession, Principal principal,
                              @RequestParam(value = "error", required = false) String error, HttpServletRequest request, RedirectAttributes attr) {
        if (principal != null) {
            return new ModelAndView(new RedirectView("/dashboard"));
        }
        ModelAndView model = new ModelAndView();
        model.addObject("captchaType", CAPTCHA_TYPE);
        if (error != null) {
            if (httpSession.getAttribute("SPRING_SECURITY_LAST_EXCEPTION") != null) {
                String[] parts = httpSession.getAttribute("SPRING_SECURITY_LAST_EXCEPTION").getClass().getName().split("\\.");
                String exceptionClass = parts[parts.length - 1];
                if (exceptionClass.equals("DisabledException")) {
                    attr.addFlashAttribute("blockedUser", messageSource.getMessage("login.blocked", null, localeResolver.resolveLocale(request)));
                    attr.addFlashAttribute("contactsUrl", "/contacts");
                } else if (exceptionClass.equals("BadCredentialsException")) {
                    attr.addFlashAttribute("loginErr", messageSource.getMessage("login.notFound", null, localeResolver.resolveLocale(request)));
                } else if (exceptionClass.equals("NotVerifiedCaptchaError")) {
                    attr.addFlashAttribute("loginErr", messageSource.getMessage("register.capchaincorrect", null, localeResolver.resolveLocale(request)));
                } else if (exceptionClass.equals("PinCodeCheckNeedException")) {
                    PinCodeCheckNeedException exception = (PinCodeCheckNeedException) httpSession.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
                    attr.addFlashAttribute("pinNeed", exception.getMessage());
                } else if (exceptionClass.equals("IncorrectPinException")) {
                    IncorrectPinException exception = (IncorrectPinException) httpSession.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
                    attr.addFlashAttribute("pinNeed", exception.getMessage());
                    attr.addFlashAttribute("pinError", messageSource.getMessage("message.pin_code.incorrect", null, localeResolver.resolveLocale(request)));
                } else if (exceptionClass.equals("BannedIpException")) {
                    BannedIpException exception = (BannedIpException) httpSession.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
                    attr.addFlashAttribute("loginErr", exception.getMessage());
                } else if (exceptionClass.equals("UnconfirmedUserException")) {
                    UnconfirmedUserException exception = (UnconfirmedUserException) httpSession.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
                    attr.addFlashAttribute("unconfirmedUserEmail", exception.getMessage());
                    attr.addFlashAttribute("unconfirmedUserMessage", messageSource.getMessage("register.unconfirmedUserMessage",
                            new Object[]{exception.getMessage()}, localeResolver.resolveLocale(request)));
                    attr.addFlashAttribute("unconfirmedUser", messageSource.getMessage("register.unconfirmedUser", null, localeResolver.resolveLocale(request)));
                } else {
                    attr.addFlashAttribute("loginErr", messageSource.getMessage("login.errorLogin", null, localeResolver.resolveLocale(request)));
                }
            }
        }

        return new ModelAndView(new RedirectView("/dashboard"));

    }

    @ResponseBody
    @RequestMapping(value = "/login/new_pin_send", method = RequestMethod.POST)
    public ResponseEntity<String> sendLoginPinAgain(HttpServletRequest request, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        Object auth = request.getSession().getAttribute("authentication");
        if (auth == null) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON_UTF8).body("error");
        }
        Authentication authentication = (Authentication) auth;
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        String res = secureService.reSendLoginMessage(request, authentication.getName(), true).getMessage();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(res);
    }

    @ResponseBody
    @RequestMapping(value = "/register/new_link_to_confirm", method = RequestMethod.POST)
    public void sendRegisterLinkAgain(@ModelAttribute("unconfirmedUserEmail") String unconfirmedUserEmail, @RequestParam(required = false) String source, Locale locale) {
        User userForSend = userService.findByEmail(unconfirmedUserEmail);
        if (source != null && !source.isEmpty()) {
            String viewForRequest = "view=" + source;
            userService.sendEmailWithToken(userForSend, TokenType.REGISTRATION, "/registrationConfirm", "emailsubmitregister.subject", "emailsubmitregister.text", locale, null, viewForRequest);
        } else {
            userService.sendEmailWithToken(userForSend, TokenType.REGISTRATION, "/registrationConfirm", "emailsubmitregister.subject", "emailsubmitregister.text", locale);
        }
    }


    @RequestMapping(value = "/referral")
    public ModelAndView referral(final Principal principal) {
        final int id = userService.getIdByEmail(principal.getName());
        return new ModelAndView("referral", singletonMap("referralTxs", referralService.findAll(id)));
    }

    @RequestMapping("/aboutUs")
    public ModelAndView aboutUs() {
        ModelAndView modelAndView = new ModelAndView("/globalPages/aboutUs", "captchaType", CAPTCHA_TYPE);
        return modelAndView;
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(WrongFinPasswordException.class)
    @ResponseBody
    public ErrorInfo WrongFinPasswordExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(AbsentFinPasswordException.class)
    @ResponseBody
    public ErrorInfo AbsentFinPasswordExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(NotConfirmedFinPasswordException.class)
    @ResponseBody
    public ErrorInfo NotConfirmedFinPasswordExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @RequestMapping(value = "/termsAndConditions", method = RequestMethod.GET)
    public ModelAndView termsAndConditions() {
        return new ModelAndView("/globalPages/termsAndConditions", "captchaType", CAPTCHA_TYPE);
    }

    @RequestMapping(value = "/privacyPolicy", method = RequestMethod.GET)
    public ModelAndView privacyPolicy() {
        return new ModelAndView("/globalPages/privacyPolicy", "captchaType", CAPTCHA_TYPE);
    }

    @RequestMapping(value = "/contacts", method = RequestMethod.GET)
    public ModelAndView contacts(ModelMap model) {
        ModelAndView modelAndView = new ModelAndView("globalPages/contacts", "captchaType", CAPTCHA_TYPE);
        model.forEach((key, value) -> logger.debug(key + " :: " + value));
        if (model.containsAttribute("messageForm")) {
            modelAndView.addObject("messageForm", model.get("messageForm"));
        } else {
            modelAndView.addObject("messageForm", new FeedbackMessageForm());
        }
        return modelAndView;
    }

    @RequestMapping(value = "/partners", method = RequestMethod.GET)
    public ModelAndView partners() {
        return new ModelAndView("/globalPages/partners", "captchaType", CAPTCHA_TYPE);
    }

    @RequestMapping(value = "/sendFeedback", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView sendFeedback(@ModelAttribute("messageForm") FeedbackMessageForm messageForm, BindingResult result,
                                     HttpServletRequest request, RedirectAttributes redirectAttributes) {


        ModelAndView modelAndView = new ModelAndView("redirect:/contacts");
        String captchaType = request.getParameter("captchaType");
        switch (captchaType) {
            case "BOTDETECT": {
                String captchaId = request.getParameter("captchaId");
                Captcha captcha = Captcha.load(request, captchaId);
                String captchaCode = request.getParameter("captchaCode");
                if (!captcha.validate(captchaCode)) {
                    String correctCapchaRequired = messageSource.getMessage("register.capchaincorrect", null, localeResolver.resolveLocale(request));
                    redirectAttributes.addFlashAttribute("errorNoty", correctCapchaRequired);
                    redirectAttributes.addFlashAttribute("messageForm", messageForm);

                    modelAndView.addObject("captchaType", CAPTCHA_TYPE);
                    return modelAndView;
                }
                break;
            }
            case "RECAPTCHA": {
                String recapchaResponse = request.getParameter("g-recaptcha-response");
                if ((recapchaResponse != null) && !verifyReCaptchaSec.verify(recapchaResponse)) {
                    String correctCapchaRequired = messageSource.getMessage("register.capchaincorrect", null, localeResolver.resolveLocale(request));
                    redirectAttributes.addFlashAttribute("errorNoty", correctCapchaRequired);
                    redirectAttributes.addFlashAttribute("messageForm", messageForm);
                    modelAndView.addObject("captchaType", CAPTCHA_TYPE);
                    return modelAndView;
                }
                break;
            }
        }
        messageFormValidator.validate(messageForm, result, localeResolver.resolveLocale(request));
        result.getAllErrors().forEach(logger::debug);
        if (result.hasErrors()) {
            modelAndView = new ModelAndView("globalPages/contacts", "messageForm", messageForm);
            modelAndView.addObject("cpch", "");
            modelAndView.addObject("captchaType", CAPTCHA_TYPE);
            return modelAndView;
        }

        sendMailService.sendFeedbackMail(messageForm.getSenderName(), messageForm.getSenderEmail(), messageForm.getMessageText(), feedbackEmail);
        redirectAttributes.addFlashAttribute("successNoty", messageSource.getMessage("contacts.lettersent", null, localeResolver.resolveLocale(request)));

        return modelAndView;
    }

    @RequestMapping(value = "/utcOffset")
    @ResponseBody
    public Integer getServerUtcOffsetMinutes() {
        return TimeZone.getDefault().getOffset(System.currentTimeMillis()) / (1000 * 60);
    }


    @RequestMapping(value = "/api_docs", method = RequestMethod.GET)
    public ModelAndView apiDocs(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("/globalPages/apiDocs", "captchaType", CAPTCHA_TYPE);
        String baseUrl = String.join("", "https://", request.getServerName(), "/openapi/v1");
        modelAndView.addObject("baseUrl", baseUrl);
        modelAndView.addObject("orderTypeValues", Arrays.asList(OrderType.values()));
        modelAndView.addObject("periodValues", Arrays.stream(OrderHistoryPeriod.values())
                .map(OrderHistoryPeriod::toUrlValue).collect(Collectors.toList()));
        Map<OpenApiMethodGroup, List<OpenApiMethodDoc>> methodGroups = Arrays.stream(OpenApiMethodDoc.values())
                .collect(Collectors.groupingBy(OpenApiMethodDoc::getMethodGroup));
        modelAndView.addObject("publicMethodsInfo", methodGroups.get(OpenApiMethodGroup.PUBLIC));
        modelAndView.addObject("userMethodsInfo", methodGroups.get(OpenApiMethodGroup.USER_INFO));
        modelAndView.addObject("orderMethodsInfo", methodGroups.get(OpenApiMethodGroup.ORDERS));
        return modelAndView;
    }

}
