package me.exrates.model.enums;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by ValkSam on 18.02.2017.
 */
public interface InvoiceStatus {

    default Set<InvoiceStatus> getAvailableNextStatesSet(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        Set<InvoiceStatus> availableNextStates = schemaMap.values().stream().collect(Collectors.toSet());
        assert (availableNextStates.size() == schemaMap.values().size());
        return availableNextStates;
    }

    default Optional<InvoiceStatus> nextState(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap, InvoiceActionTypeEnum action) {
        return Optional.ofNullable(schemaMap.get(action));
    }

    InvoiceStatus nextState(InvoiceActionTypeEnum action);

    default Boolean availableForAction(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap, InvoiceActionTypeEnum action) {
        return schemaMap.get(action) != null;
    }

    InvoiceStatus nextState(InvoiceActionTypeEnum action, InvoiceActionTypeEnum.InvoiceActionParamsValue paramsValue);

    Boolean availableForAction(InvoiceActionTypeEnum action);

    Set<InvoiceActionTypeEnum> getAvailableActionList();

    Set<InvoiceActionTypeEnum> getAvailableActionList(InvoiceActionTypeEnum.InvoiceActionParamsValue paramsValue);

    void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap);

    Boolean isEndStatus();

    Boolean isSuccessEndStatus();

    Integer getCode();

    String name();

}
