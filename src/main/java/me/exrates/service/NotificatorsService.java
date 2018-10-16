package me.exrates.service;

import me.exrates.model.dto.Notificator;
import me.exrates.model.dto.NotificatorTotalPriceDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface NotificatorsService {

    NotificatorService getNotificationService(Integer notificatorId);

    Map<Integer, Object> getSubscriptions(int userId);

    Subscribable getByNotificatorId(int id);

    Notificator getById(int id);

    BigDecimal getMessagePrice(int notificatorId, int roleId);

    NotificatorTotalPriceDto getPrices(int notificatorId, int roleId);

    BigDecimal getSubscriptionPrice(int notificatorId, int roleId);

    List<Notificator> getAllNotificators();

    NotificatorService getNotificationServiceByBeanName(String beanName);

}
