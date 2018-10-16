package me.exrates.model.enums;

import lombok.NoArgsConstructor;
import me.exrates.exception.UnsupportedBusinessUserRoleNameException;

import java.util.Arrays;

/**
 * Created by ValkSam
 */
@NoArgsConstructor
public enum BusinessUserRoleEnum {
    ADMIN,
    USER,
    EXCHANGE,
    VIP_USER,
    TRADER,
    BOT,
    MARKET_MAKER;


    public static BusinessUserRoleEnum convert(String name) {
        return Arrays.stream(BusinessUserRoleEnum.class.getEnumConstants())
                .filter(e -> e.name().equals(name))
                .findAny()
                .orElseThrow(() -> new UnsupportedBusinessUserRoleNameException(name));
    }
}
