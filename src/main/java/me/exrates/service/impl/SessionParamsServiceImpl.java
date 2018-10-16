package me.exrates.service.impl;

import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.SessionParamsDao;
import me.exrates.model.SessionLifeTimeType;
import me.exrates.model.SessionParams;
import me.exrates.model.enums.SessionLifeTypeEnum;
import me.exrates.service.SessionParamsService;
import me.exrates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;

@Log4j2
@Service
@PropertySource("classpath:session.properties")
public class SessionParamsServiceImpl implements SessionParamsService {

    private @Value("${session.default_session_lifetime_minutes}") int defaultSessionLifetimeMinutes;
    private @Value("${session.lifeTypeParamName}") String sessionLifeTimeParamName;
    private @Value("${session.timeParamName}") String sessionTimeMinutesParamName;
    private @Value("${session.lastRequestParamName}") String sessionLastRequestParamName;
    private static final int MIN_SESSION_TIME_MINUTES = 5;
    private static final int MAX_SESSION_TIME_MINUTES = 1440;

    @Autowired
    private SessionParamsDao sessionParamsDao;
    @Autowired
    private UserService userService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private LocaleResolver localeResolver;

    @Override
    public List<SessionLifeTimeType> getAllByActive(boolean active) {
        return sessionParamsDao.getAllByActive(active);
    }

    @Override
    public SessionParams getByUserEmail(String userEmail) {
        return sessionParamsDao.getByUserEmail(userEmail);
    }

    @Override
    public SessionParams getByEmailOrDefault(String email) {
        SessionParams params = this.getByUserEmail(email);
        log.info("params in service {}", params);
        return params == null ? getDefaultSessionPararms() : params;
    }

    @Transactional
    @Override
    public SessionParams saveOrUpdate(SessionParams sessionParams, String userEmail) {
        SessionParams oldParams = this.getByUserEmail(userEmail);
        if (oldParams == null) {
            sessionParams.setId(null);
            sessionParams.setUserId(userService.getIdByEmail(userEmail));
            sessionParamsDao.create(sessionParams);
        } else {
            sessionParams.setId(oldParams.getId());
            sessionParamsDao.update(sessionParams);
        }
        return null;
    }

    @Override
    public SessionParams determineSessionParams() {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        if (principal != null) {
            return this.getByEmailOrDefault(principal.getName());
        } else {
            return this.getDefaultSessionPararms();
        }
    }

    private SessionParams getDefaultSessionPararms() {
        return new SessionParams(defaultSessionLifetimeMinutes, SessionLifeTypeEnum.INACTIVE_COUNT_LIFETIME.getTypeId());
    }

    @Override
    public boolean isSessionTimeValid(int sessionTime) {
        return sessionTime >= MIN_SESSION_TIME_MINUTES && sessionTime <= MAX_SESSION_TIME_MINUTES;
    }

    @Override
    public boolean isSessionLifeTypeIdValid(int typeId) {
        try {
            SessionLifeTypeEnum.convert(typeId);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean islifeTypeActive(int sessionLifeTypeId) {
        List<SessionLifeTimeType> typeList = this.getAllByActive(true);
        return typeList.stream().anyMatch(p -> p.getId() == sessionLifeTypeId && p.isAvailable());
    }

    @Override
    public void setSessionLifeParams(HttpServletRequest request) {
        HttpSession session = request.getSession();
        SessionParams params = this.determineSessionParams();
        if (!this.islifeTypeActive(params.getSessionLifeTypeId())) {
            params.setSessionLifeTypeId(SessionLifeTypeEnum.INACTIVE_COUNT_LIFETIME.getTypeId());
        }
        System.out.println("time " + params.getSessionTimeMinutes());
        session.setAttribute(sessionTimeMinutesParamName, params.getSessionTimeMinutes());
        session.setAttribute(sessionLifeTimeParamName, params.getSessionLifeTypeId());
        session.setAttribute(sessionLastRequestParamName, System.currentTimeMillis());
    }

}
