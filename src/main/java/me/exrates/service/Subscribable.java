package me.exrates.service;

import me.exrates.model.dto.NotificatorSubscription;
import org.springframework.transaction.annotation.Transactional;

public interface Subscribable {

    Object subscribe(Object subscriptionObject);

    Object createSubscription(String email);

    Object prepareSubscription(Object subscriptionObject);

    NotificatorSubscription getSubscription(int userId);

    @Transactional
    Object reconnect(Object userEmail);
}
