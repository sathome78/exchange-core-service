package me.exrates.model.enums;


import lombok.extern.log4j.Log4j2;
import me.exrates.exception.UnsupportedInvoiceStatusForActionException;
import me.exrates.exception.UnsupportedWithdrawRequestStatusIdException;
import me.exrates.exception.UnsupportedWithdrawRequestStatusNameException;

import java.util.*;
import java.util.stream.Collectors;

import static me.exrates.model.enums.InvoiceActionTypeEnum.*;

@Log4j2
public enum RefillStatusEnum implements InvoiceStatus {
    X_STATE(0) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(CREATE_BY_USER, CREATED_USER);
            schemaMap.put(CREATE_BY_FACT, CREATED_BY_FACT);
        }
    },
    CREATED_USER(1) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(PUT_FOR_CONFIRM_USER, WAITING_CONFIRMATION_USER);
            schemaMap.put(PUT_FOR_PENDING, ON_PENDING);
        }
    },
    CREATED_BY_FACT(2) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(START_BCH_EXAMINE, ON_BCH_EXAM);
            schemaMap.put(ACCEPT_AUTO, ACCEPTED_AUTO);
            schemaMap.put(REQUEST_INNER_TRANSFER, ON_INNER_TRANSFERRING);
        }
    },
    WAITING_CONFIRMATION_USER(3) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.CONFIRM_USER, CONFIRMED_USER);
            schemaMap.put(InvoiceActionTypeEnum.REVOKE, REVOKED_USER);
            schemaMap.put(InvoiceActionTypeEnum.EXPIRE, EXPIRED);
        }
    },
    ON_PENDING(4) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.ACCEPT_AUTO, ACCEPTED_AUTO);
            schemaMap.put(InvoiceActionTypeEnum.TAKE_TO_WORK, TAKEN_FROM_PENDING);
            schemaMap.put(InvoiceActionTypeEnum.REQUEST_INNER_TRANSFER, ON_INNER_TRANSFERRING);
            schemaMap.put(InvoiceActionTypeEnum.START_BCH_EXAMINE, ON_BCH_EXAM);
            schemaMap.put(InvoiceActionTypeEnum.REVOKE, REVOKED_USER);
            schemaMap.put(InvoiceActionTypeEnum.EXPIRE, EXPIRED);
        }
    },
    CONFIRMED_USER(5) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.TAKE_TO_WORK, IN_WORK_OF_ADMIN);
        }
    },
    ON_BCH_EXAM(6) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.ACCEPT_AUTO, ACCEPTED_AUTO);
            schemaMap.put(InvoiceActionTypeEnum.TAKE_TO_WORK, TAKEN_FROM_EXAM);
            schemaMap.put(InvoiceActionTypeEnum.DECLINE_MERCHANT, WAITING_REVIEWING);
        }
    },
    IN_WORK_OF_ADMIN(7) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.ACCEPT_HOLDED, ACCEPTED_ADMIN);
            schemaMap.put(InvoiceActionTypeEnum.DECLINE_HOLDED, DECLINED_ADMIN);
            schemaMap.put(InvoiceActionTypeEnum.RETURN_FROM_WORK, CONFIRMED_USER);
        }
    },
    DECLINED_ADMIN(8) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.CONFIRM_USER, CONFIRMED_USER);
            schemaMap.put(InvoiceActionTypeEnum.REVOKE, REVOKED_USER);
        }
    },
    ACCEPTED_AUTO(9) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        }
    },
    ACCEPTED_ADMIN(10) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        }
    },
    REVOKED_USER(11) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        }
    },
    EXPIRED(12) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        }
    },
    TAKEN_FROM_PENDING(13) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.ACCEPT_HOLDED, ACCEPTED_ADMIN);
            schemaMap.put(InvoiceActionTypeEnum.RETURN_FROM_WORK, ON_PENDING);
        }
    },
    TAKEN_FROM_EXAM(14) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.ACCEPT_HOLDED, ACCEPTED_ADMIN);
            schemaMap.put(InvoiceActionTypeEnum.RETURN_FROM_WORK, ON_BCH_EXAM);
        }
    },
    ON_INNER_TRANSFERRING(15) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.START_BCH_EXAMINE, ON_BCH_EXAM);
            schemaMap.put(InvoiceActionTypeEnum.REJECT_TO_REVIEW, WAITING_REVIEWING);
        }
    },
    WAITING_REVIEWING(16) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.TAKE_TO_WORK, TAKEN_FOR_REFILL);
        }
    },
    TAKEN_FOR_REFILL(17) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
            schemaMap.put(InvoiceActionTypeEnum.RETURN_FROM_WORK, WAITING_REVIEWING);
            schemaMap.put(InvoiceActionTypeEnum.ACCEPT_HOLDED, ACCEPTED_ADMIN);
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
        for (RefillStatusEnum status : RefillStatusEnum.class.getEnumConstants()) {
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

    public static RefillStatusEnum convert(int id) {
        return Arrays.stream(RefillStatusEnum.class.getEnumConstants())
                .filter(e -> e.code == id)
                .findAny()
                .orElseThrow(() -> new UnsupportedWithdrawRequestStatusIdException(String.valueOf(id)));
    }

    public static RefillStatusEnum convert(String name) {
        return Arrays.stream(RefillStatusEnum.class.getEnumConstants())
                .filter(e -> e.name().equals(name))
                .findAny()
                .orElseThrow(() -> new UnsupportedWithdrawRequestStatusNameException(name));
    }

    public static InvoiceStatus getBeginState() {
        Set<InvoiceStatus> allNodesSet = collectAllSchemaMapNodesSet();
        List<InvoiceStatus> candidateList = Arrays.stream(RefillStatusEnum.class.getEnumConstants())
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

    private static Set<InvoiceStatus> collectAllSchemaMapNodesSet() {
        Set<InvoiceStatus> result = new HashSet<>();
        Arrays.stream(RefillStatusEnum.class.getEnumConstants())
                .forEach(e -> result.addAll(e.schemaMap.values()));
        return result;
    }

    private Integer code;

    RefillStatusEnum(Integer code) {
        this.code = code;
    }

    @Override
    public Integer getCode() {
        return code;
    }

}

