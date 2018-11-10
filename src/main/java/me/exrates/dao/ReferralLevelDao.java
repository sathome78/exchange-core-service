package me.exrates.dao;

import me.exrates.model.ReferralLevel;

import java.math.BigDecimal;
import java.util.List;

public interface ReferralLevelDao {

    List<ReferralLevel> findAll();

    int create(ReferralLevel level);

    void delete(int levelId);
}
