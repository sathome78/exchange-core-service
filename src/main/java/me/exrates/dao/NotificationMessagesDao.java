package me.exrates.dao;

import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.NotificationTypeEnum;

/**
 * Created by Maks on 02.10.2017.
 */
public interface NotificationMessagesDao {

    String gerResourceString(NotificationMessageEventEnum event, NotificationTypeEnum typeEnum);

}
