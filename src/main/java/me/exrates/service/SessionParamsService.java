package me.exrates.service;

import me.exrates.model.SessionLifeTimeType;
import me.exrates.model.SessionParams;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface SessionParamsService {
    List<SessionLifeTimeType> getAllByActive(boolean active);

    SessionParams getByUserEmail(String userEmail);

    SessionParams getByEmailOrDefault(String email);

    SessionParams saveOrUpdate(SessionParams sessionParams, String userEmail);

    SessionParams determineSessionParams();

    boolean isSessionTimeValid(int sessionTime);

    boolean isSessionLifeTypeIdValid(int typeId);

    boolean islifeTypeActive(int sessionLifeTypeId);

    void setSessionLifeParams(HttpServletRequest request);
}
