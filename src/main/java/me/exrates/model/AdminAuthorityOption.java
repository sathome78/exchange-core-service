package me.exrates.model;

import lombok.Data;
import me.exrates.model.enums.AdminAuthority;
import org.springframework.context.MessageSource;

import java.util.Locale;

@Data
public class AdminAuthorityOption {
    private AdminAuthority adminAuthority;
    private Boolean enabled;

    private String adminAuthorityLocalized;
}
