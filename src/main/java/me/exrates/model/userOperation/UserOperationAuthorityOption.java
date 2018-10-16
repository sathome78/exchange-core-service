package me.exrates.model.userOperation;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import org.springframework.context.MessageSource;

import java.util.Locale;

@Getter
@Setter
public class UserOperationAuthorityOption {
    private UserOperationAuthority userOperationAuthority;
    private Boolean enabled;

    private String userOperationAuthorityLocalized;

    public void localize(MessageSource messageSource, Locale locale) {
        userOperationAuthorityLocalized = userOperationAuthority.toString(messageSource, locale);
    }

    @Override
    public String toString() {
        return "UserOperationAuthorityOption{" +
                ", userOperationAuthority=" + userOperationAuthority +
                ", enabled=" + enabled +
                '}';
    }
}
