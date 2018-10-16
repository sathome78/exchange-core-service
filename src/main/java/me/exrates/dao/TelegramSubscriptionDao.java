package me.exrates.dao;

import me.exrates.model.dto.TelegramSubscription;

import java.util.Optional;

/**
 * Created by Maks on 05.10.2017.
 */
public interface TelegramSubscriptionDao {

    Optional<TelegramSubscription> getSubscribtionByCodeAndEmail(String code, String email);

    TelegramSubscription getSubscribtionByUserId(int userId);

    void updateSubscription(TelegramSubscription subscribtion);

    int create(TelegramSubscription subscription);

    void updateCode(String code, int userId);
}
