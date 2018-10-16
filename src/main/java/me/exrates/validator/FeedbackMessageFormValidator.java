package me.exrates.validator;

import me.exrates.model.form.FeedbackMessageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ogolv on 09.08.2016.
 */
@Component
public class FeedbackMessageFormValidator implements Validator {
    private Pattern pattern;
    private Matcher matcher;
    private static final int MIN_NAME_SIZE = 2;
    private static final int MAX_NAME_SIZE = 30;
    private static final int MIN_MESSAGE_SIZE = 2;
    private static final int MAX_MESSAGE_SIZE = 500;

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @Autowired
    MessageSource messageSource;

    private Locale locale = new Locale("en");


    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    public void validate(Object target, Errors errors, Locale locale) {
        this.locale = locale;
        validate(target, errors);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FeedbackMessageForm messageForm = (FeedbackMessageForm) target;
        String senderNameRequired = messageSource.getMessage("validation.nameRequired", null, locale);
        String emailRequired = messageSource.getMessage("validation.emailrequired", null, locale);
        String emailIncorrect = messageSource.getMessage("validation.emailincorrect", null, locale);
        String messageRequired = messageSource.getMessage("validation.messageRequired", null, locale);
        String senderNameIncorrectSize = messageSource.getMessage("validation.nameIncorrectSize", null, locale);
        String messageIncorrectSize = messageSource.getMessage("validation.messageIncorrectSize", null, locale);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "senderName", "required.senderName",
                senderNameRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "senderEmail", "required.senderEmail",
                emailRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "messageText", "required.messageText",
                messageRequired);
        if (messageForm.getSenderName().length() < MIN_NAME_SIZE || messageForm.getSenderName().length() > MAX_NAME_SIZE) {
            errors.rejectValue("senderName", "senderName.incorrectSize", senderNameIncorrectSize);
        }
        if (messageForm.getMessageText().length() < MIN_MESSAGE_SIZE || messageForm.getMessageText().length() > MAX_MESSAGE_SIZE) {
            errors.rejectValue("messageText", "messageText.incorrectSize", messageIncorrectSize);
        }

        if (!(messageForm.getSenderEmail() != null && messageForm.getSenderEmail().isEmpty())) {
            pattern = Pattern.compile(EMAIL_PATTERN);
            matcher = pattern.matcher(messageForm.getSenderEmail());
            if (!matcher.matches()) {
                errors.rejectValue("senderEmail", "email.incorrect", emailIncorrect);
            }
        }

    }
}
