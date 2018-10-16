package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.UserRole;

@Getter
@Setter
@ToString
public class UserRoleSettings {
    private UserRole userRole;
    private boolean isOrderAcceptionSameRoleOnly;
    private boolean isBotAcceptionAllowedOnly;
    private boolean isManualChangeAllowed;
    private boolean isConsideredForPriceRange;
}
