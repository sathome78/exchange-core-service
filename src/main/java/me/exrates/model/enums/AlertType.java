package me.exrates.model.enums;

/**
 * Created by Maks on 13.12.2017.
 */
public enum AlertType {

    UPDATE("message.alertUpdate", true), TECHNICAL_WORKS("message.alertTechWorks", false);

    private String messageTmpl;
    private boolean needDateTime;

    public String getMessageTmpl() {
        return messageTmpl;
    }

    public boolean isNeedDateTime() {
        return needDateTime;
    }

    AlertType(String messageTmpl, boolean needDateTime) {
        this.messageTmpl = messageTmpl;
        this.needDateTime = needDateTime;
    }
}
