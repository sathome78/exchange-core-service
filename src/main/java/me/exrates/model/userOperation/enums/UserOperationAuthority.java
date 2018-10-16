package me.exrates.model.userOperation.enums;

import me.exrates.exception.UnsupportedAuthorityException;
import org.springframework.context.MessageSource;

import java.util.Arrays;
import java.util.Locale;

public enum UserOperationAuthority {
    INPUT(1),
    OUTPUT(2),
    TRANSFER(3),
    TRADING(4);

    public final int operationId;

    UserOperationAuthority(int operationId) {
        this.operationId = operationId;
    }

    public int getOperationId() {
        return operationId;
    }

    public static UserOperationAuthority convert(int operationId) {
        return Arrays.stream(UserOperationAuthority.values())
                .filter(auth -> auth.getOperationId() == operationId)
                .findAny().orElseThrow(() -> new UnsupportedAuthorityException("Unsupported operation of authority"));
    }

    public String toString(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage("userOperation." + this.name(), null, locale);
    }
}
