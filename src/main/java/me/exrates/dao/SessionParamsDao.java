package me.exrates.dao;

import me.exrates.model.SessionLifeTimeType;
import me.exrates.model.SessionParams;

import java.util.List;

public interface SessionParamsDao {

    List<SessionLifeTimeType> getAllByActive(boolean active);

    SessionParams getByUserEmail(String userEmail);

    SessionParams create(SessionParams sessionLifeType);

    void update(SessionParams sessionParams);
}
