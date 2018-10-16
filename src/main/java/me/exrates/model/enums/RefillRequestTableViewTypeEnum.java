package me.exrates.model.enums;


import me.exrates.exception.UnsupportedWithdrawRequestTableViewTypeNameException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.exrates.model.enums.RefillStatusEnum.*;

public enum RefillRequestTableViewTypeEnum {

  ALL,
  FOR_WORK(CONFIRMED_USER, IN_WORK_OF_ADMIN, TAKEN_FROM_PENDING, TAKEN_FROM_EXAM),
  WAIT_PAYMENT(ON_PENDING),
  COLLECT_CONFIRMATIONS(ON_BCH_EXAM),
  ACCEPTED(ACCEPTED_ADMIN, ACCEPTED_AUTO),
  DECLINED(DECLINED_ADMIN);

  private List<RefillStatusEnum> refillStatusList = new ArrayList<>();

  RefillRequestTableViewTypeEnum(RefillStatusEnum... refillStatusEnum) {
    refillStatusList.addAll(Arrays.asList(refillStatusEnum));
  }

  public List<RefillStatusEnum> getRefillStatusList() {
    return refillStatusList;
  }

  public static RefillRequestTableViewTypeEnum convert(String name) {
    return Arrays.stream(RefillRequestTableViewTypeEnum.class.getEnumConstants())
        .filter(e -> e.name().equals(name))
        .findAny()
        .orElseThrow(() -> new UnsupportedWithdrawRequestTableViewTypeNameException(name));
  }
}
