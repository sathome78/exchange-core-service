package me.exrates.model.enums;


import lombok.extern.log4j.Log4j2;
import me.exrates.exception.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static me.exrates.model.enums.InvoiceActionTypeEnum.*;

@Log4j2
public enum WithdrawStatusEnum implements InvoiceStatus {
    CREATED_USER(1) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(PUT_FOR_MANUAL, WAITING_MANUAL_POSTING);
            schemaMap.put(PUT_FOR_AUTO, WAITING_AUTO_POSTING);
            schemaMap.put(PUT_FOR_CONFIRM, WAITING_CONFIRMATION);
        }
    },
    WAITING_MANUAL_POSTING(2) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.TAKE_TO_WORK, IN_WORK_OF_ADMIN);
            schemaMap.put(InvoiceActionTypeEnum.REVOKE, REVOKED_USER);
        }
    },
    WAITING_AUTO_POSTING(3) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.DECLINE, DECLINED_ADMIN);
            schemaMap.put(InvoiceActionTypeEnum.HOLD_TO_POST, IN_POSTING);
            schemaMap.put(InvoiceActionTypeEnum.REVOKE, REVOKED_USER);
        }
    },
    WAITING_CONFIRMATION(4) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.DECLINE, DECLINED_ADMIN);
            schemaMap.put(InvoiceActionTypeEnum.CONFIRM_ADMIN, WAITING_CONFIRMED_POSTING);
            schemaMap.put(InvoiceActionTypeEnum.REVOKE, REVOKED_USER);
        }
    },
    IN_WORK_OF_ADMIN(5) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.DECLINE_HOLDED, DECLINED_ADMIN);
            schemaMap.put(InvoiceActionTypeEnum.POST_HOLDED, POSTED_MANUAL);
            schemaMap.put(InvoiceActionTypeEnum.RETURN_FROM_WORK, WAITING_MANUAL_POSTING);
        }
    },
    WAITING_CONFIRMED_POSTING(6) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.DECLINE, DECLINED_ADMIN);
            schemaMap.put(InvoiceActionTypeEnum.HOLD_TO_POST, IN_POSTING);
            schemaMap.put(InvoiceActionTypeEnum.REVOKE, REVOKED_USER);
        }
    },
    REVOKED_USER(7) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        }
    },
    DECLINED_ADMIN(8) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        }
    },
    POSTED_MANUAL(9) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        }
    },
    POSTED_AUTO(10) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        }
    },
    IN_POSTING(11) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.START_BCH_EXAMINE, ON_BCH_EXAM);
            schemaMap.put(InvoiceActionTypeEnum.POST_AUTO, POSTED_AUTO);
            schemaMap.put(InvoiceActionTypeEnum.REJECT_TO_REVIEW, WAITING_REVIEWING);
            schemaMap.put(InvoiceActionTypeEnum.REJECT_ERROR, DECLINED_ERROR);
        }
    },
    DECLINED_ERROR(12) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        }
    },
    ON_BCH_EXAM(13) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.FINALIZE_POST, POSTED_AUTO);
            schemaMap.put(InvoiceActionTypeEnum.REJECT_TO_REVIEW, WAITING_REVIEWING);
        }
    },
    WAITING_REVIEWING(14) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.TAKE_TO_WORK, TAKEN_FOR_WITHDRAW);
        }
    },
    TAKEN_FOR_WITHDRAW(15) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.DECLINE_HOLDED, DECLINED_ADMIN);
            schemaMap.put(InvoiceActionTypeEnum.POST_HOLDED, POSTED_MANUAL);
            schemaMap.put(InvoiceActionTypeEnum.RETURN_FROM_WORK, WAITING_REVIEWING);
        }
    };

    final private Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap = new HashMap<>();

    @Override
    public InvoiceStatus nextState(InvoiceActionTypeEnum action) {
        action.checkRestrictParamNeeded();
        return nextState(schemaMap, action)
                .orElseThrow(() -> new UnsupportedInvoiceStatusForActionException(String.format("current state: %s action: %s", this.name(), action.name())));
    }

    @Override
    public Boolean availableForAction(InvoiceActionTypeEnum action) {
        return availableForAction(schemaMap, action);
    }

    static {
        for (WithdrawStatusEnum status : WithdrawStatusEnum.class.getEnumConstants()) {
            status.initSchema(status.schemaMap);
        }
        /*check schemaMap*/
        getBeginState();
    }

    public Set<InvoiceActionTypeEnum> getAvailableActionList(InvoiceActionTypeEnum.InvoiceActionParamsValue paramsValue) {
        return schemaMap.keySet().stream()
                .filter(e -> e.isMatchesTheParamsValue(paramsValue))
                .collect(Collectors.toSet());
    }

    /**/

    public static WithdrawStatusEnum convert(int id) {
        return Arrays.stream(WithdrawStatusEnum.class.getEnumConstants())
                .filter(e -> e.code == id)
                .findAny()
                .orElseThrow(() -> new UnsupportedWithdrawRequestStatusIdException(String.valueOf(id)));
    }

    public static WithdrawStatusEnum convert(String name) {
        return Arrays.stream(WithdrawStatusEnum.class.getEnumConstants())
                .filter(e -> e.name().equals(name))
                .findAny()
                .orElseThrow(() -> new UnsupportedWithdrawRequestStatusNameException(name));
    }

    public static InvoiceStatus getBeginState() {
        Set<InvoiceStatus> allNodesSet = collectAllSchemaMapNodesSet();
        List<InvoiceStatus> candidateList = Arrays.stream(WithdrawStatusEnum.class.getEnumConstants())
                .filter(e -> !allNodesSet.contains(e))
                .collect(Collectors.toList());
        if (candidateList.size() == 0) {
            log.fatal("begin state not found");
            throw new AssertionError();
        }
        if (candidateList.size() > 1) {
            log.fatal("more than single begin state found: " + candidateList);
            throw new AssertionError();
        }
        return candidateList.get(0);
    }

    public static Set<InvoiceStatus> getEndStatesSet() {
        return Arrays.stream(WithdrawStatusEnum.class.getEnumConstants())
                .filter(e -> e.schemaMap.isEmpty())
                .collect(Collectors.toSet());
    }

    private static Set<InvoiceStatus> collectAllSchemaMapNodesSet() {
        Set<InvoiceStatus> result = new HashSet<>();
        Arrays.stream(WithdrawStatusEnum.class.getEnumConstants())
                .forEach(e -> result.addAll(e.schemaMap.values()));
        return result;
    }

    private Integer code;

    WithdrawStatusEnum(Integer code) {
        this.code = code;
    }

    @Override
    public Integer getCode() {
        return code;
    }

}

