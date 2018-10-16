package me.exrates.model;

import lombok.Data;
import me.exrates.model.enums.AdminAuthority;

@Data
public class AdminAuthorityOption {
    private AdminAuthority adminAuthority;
    private Boolean enabled;

    private String adminAuthorityLocalized;
}
