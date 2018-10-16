package me.exrates.dao;

import me.exrates.model.SessionLifeTimeType;
import me.exrates.model.SessionParams;

import java.util.List;

/**
 * Created by maks on 31.03.2017.
 */
public interface SessionParamsDao {

    List<SessionLifeTimeType> getAllByActive(boolean active);

    SessionParams getByUserEmail(String userEmail);

    SessionParams create(SessionParams sessionLifeType);

    void update(SessionParams sessionParams);
}
