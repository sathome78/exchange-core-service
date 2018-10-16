package me.exrates.model.enums;

import me.exrates.exception.UnsupportedWithdrawRequestTableViewTypeNameException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.exrates.model.enums.WithdrawStatusEnum.*;

public enum WithdrawRequestTableViewTypeEnum {

    ALL,
    FOR_WORK(WAITING_MANUAL_POSTING, IN_WORK_OF_ADMIN, WAITING_CONFIRMATION),
    FOR_MANUAL(WAITING_MANUAL_POSTING, IN_WORK_OF_ADMIN, WAITING_CONFIRMATION),
    FOR_CONFIRM(WAITING_CONFIRMATION),
    AUTO_PROCESSING(WAITING_AUTO_POSTING, WAITING_CONFIRMED_POSTING),
    POSTED(POSTED_AUTO, POSTED_MANUAL),
    DECLINED(DECLINED_ADMIN, DECLINED_ERROR),
    FOR_CHECKING(WAITING_REVIEWING, TAKEN_FOR_WITHDRAW);

    private List<WithdrawStatusEnum> withdrawStatusList = new ArrayList<>();

    WithdrawRequestTableViewTypeEnum(WithdrawStatusEnum... withdrawStatusEnum) {
        withdrawStatusList.addAll(Arrays.asList(withdrawStatusEnum));
    }

    public static WithdrawRequestTableViewTypeEnum convert(String name) {
        return Arrays.stream(WithdrawRequestTableViewTypeEnum.class.getEnumConstants())
                .filter(e -> e.name().equals(name))
                .findAny()
                .orElseThrow(() -> new UnsupportedWithdrawRequestTableViewTypeNameException(name));
    }

    public List<WithdrawStatusEnum> getWithdrawStatusList() {
        return withdrawStatusList;
    }
}
