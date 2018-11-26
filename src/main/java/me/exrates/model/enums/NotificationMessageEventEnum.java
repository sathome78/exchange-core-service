package me.exrates.model.enums;

import java.util.stream.Stream;

public enum NotificationMessageEventEnum {

    LOGIN(1, "message.pincode.forlogin", "message.subj.login.pin", false, true),
    WITHDRAW(2, "message.pincode.forWithdraw", "message.subj.withdraw.pin", false, true),
    TRANSFER(3, "message.pincode.forTransfer", "message.subj.transfer.pin", false, true),
    CHANGE_2FA_SETTING(4, "message.pincode.for2faChange", "message.subj.2fachange.pin", false, false);

    private int code;

    private boolean canBeDisabled;

    private boolean isChangable;

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

    public boolean isChangable() {
        return isChangable;
    }

    NotificationMessageEventEnum(int code, String messageCode, String sbjCode, boolean canBeDisabled, boolean isChangable) {
        this.code = code;
        this.messageCode = messageCode;
        this.sbjCode = sbjCode;
        this.canBeDisabled = canBeDisabled;
        this.isChangable = isChangable;
    }

    public static NotificationMessageEventEnum convert(int id) {
        return Stream.of(NotificationMessageEventEnum.values()).filter(item -> item.code == id).findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("id: %s", id)));
    }

    public static NotificationMessageEventEnum convert(String value) {
        switch (value) {
            case "TRANSFER" :   return TRANSFER;
            case "WITHDRAW" :   return WITHDRAW;
            case "LOGIN" :      return LOGIN;
            default: return null;
        }
    }
}
