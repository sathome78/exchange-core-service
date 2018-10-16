package me.exrates.model.form;

/**
 * Created by ogolv on 09.08.2016.
 */
public class FeedbackMessageForm {

    private String senderName;
    private String senderEmail;
    private String messageText;

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    @Override
    public String toString() {
        return "FeedbackMessageForm{" +
                "senderName='" + senderName + '\'' +
                ", senderEmail='" + senderEmail + '\'' +
                ", messageText='" + messageText + '\'' +
                '}';
    }
}
