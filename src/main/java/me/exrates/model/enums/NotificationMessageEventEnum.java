package me.exrates.model.enums;

import java.util.stream.Stream;

/**
 * Created by Maks on 29.09.2017.
 */
public enum NotificationMessageEventEnum {

    LOGIN(1, "message.pincode.forlogin", "message.subj.login.pin", true),
    WITHDRAW(2, "message.pincode.forWithdraw", "message.subj.withdraw.pin", false),
    TRANSFER(3, "message.pincode.forTransfer", "message.subj.transfer.pin", false);

    private int code;

    private boolean canBeDisabled;

    private String messageCode;

    private String sbjCode;

    public int getCode() {
        return code;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public String getSbjCode() {
        return sbjCode;
    }

    public boolean isCanBeDisabled() {
        return canBeDisabled;
    }

    NotificationMessageEventEnum(int code, String messageCode, String sbjCode, boolean canBeDisabled) {
        this.code = code;
        this.messageCode = messageCode;
        this.sbjCode = sbjCode;
        this.canBeDisabled = canBeDisabled;
    }

    public static NotificationMessageEventEnum convert(int id) {
        return Stream.of(NotificationMessageEventEnum.values()).filter(item -> item.code == id).findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("id: %s", id)));
    }
}
