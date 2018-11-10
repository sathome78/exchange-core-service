package me.exrates.dao;

import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.NotificationTypeEnum;

public interface NotificationMessagesDao {

    String gerResourceString(NotificationMessageEventEnum event, NotificationTypeEnum typeEnum);

}
