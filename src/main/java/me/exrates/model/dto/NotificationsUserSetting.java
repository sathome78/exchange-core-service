package me.exrates.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import me.exrates.model.enums.NotificationMessageEventEnum;

@Data
@Builder
public class NotificationsUserSetting {

    private Integer id;
    private int userId;
    private NotificationMessageEventEnum notificationMessageEventEnum;
    private Integer notificatorId;

    @Tolerate
    public NotificationsUserSetting() {
    }
}
