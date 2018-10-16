package me.exrates.model.enums;

import me.exrates.exception.UnsupportedAuthorityException;
import org.springframework.context.MessageSource;

import java.util.Arrays;
import java.util.Locale;

public enum AdminAuthority {
    PROCESS_WITHDRAW(1),
    PROCESS_INVOICE(2),
    DELETE_ORDER(3),
    COMMENT_USER(4),
    MANAGE_SESSIONS(5),
    SET_CURRENCY_LIMIT(6),
    MANAGE_ACCESS(7),
    MANUAL_BALANCE_CHANGE(8),
    EDIT_USER(9),
    MANAGE_BTC_CORE_WALLET(10),
    SEE_REPORTS(11);

    private final int authority;

    AdminAuthority(int authority) {
        this.authority = authority;
    }

    public int getAuthority() {
        return authority;
    }

    public static AdminAuthority convert(int authority) {
        return Arrays.stream(AdminAuthority.values())
                .filter(auth -> auth.getAuthority() == authority)
                .findAny().orElseThrow(() -> new UnsupportedAuthorityException("Unsupported type of authority"));
    }

    public String toLocalizedString(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage("adminAuthority." + this.name(), null, locale);
    }

}
