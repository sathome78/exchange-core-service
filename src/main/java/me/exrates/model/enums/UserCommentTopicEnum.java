package me.exrates.model.enums;


import lombok.extern.log4j.Log4j2;
import me.exrates.exception.UnsupportedUserCommentTopicIdException;
import me.exrates.exception.UnsupportedUserCommentTopicNameException;

import java.util.Arrays;

@Log4j2
public enum UserCommentTopicEnum {
    GENERAL(1),
    REFILL_DECLINE(2),
    REFILL_CURRENCY_WARNING(3),
    WITHDRAW_DECLINE(4),
    WITHDRAW_POSTED(5),
    WITHDRAW_CURRENCY_WARNING(6),
    REFILL_ACCEPTED(7),
    INITIAL_TRANSFER_CURRENCY_WARNING(8),
    TRANSFER_CURRENCY_WARNING(9),
    REFILL_MERCHANT_WARNING(10),
    WITHDRAW_MERCHANT_WARNING(11);

    private Integer code;

    UserCommentTopicEnum(Integer code) {
        this.code = code;
    }

    public static UserCommentTopicEnum convert(int id) {
        return Arrays.stream(UserCommentTopicEnum.class.getEnumConstants())
                .filter(e -> e.code == id)
                .findAny()
                .orElseThrow(() -> new UnsupportedUserCommentTopicIdException(String.valueOf(id)));
    }

    public static UserCommentTopicEnum convert(String name) {
        return Arrays.stream(UserCommentTopicEnum.class.getEnumConstants())
                .filter(e -> e.name().equals(name))
                .findAny()
                .orElseThrow(() -> new UnsupportedUserCommentTopicNameException(name));
    }

    public Integer getCode() {
        return code;
    }
}
