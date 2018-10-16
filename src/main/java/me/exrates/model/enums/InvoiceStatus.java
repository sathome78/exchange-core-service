package me.exrates.model.enums;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface InvoiceStatus {

    default Optional<InvoiceStatus> nextState(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap, InvoiceActionTypeEnum action) {
        return Optional.ofNullable(schemaMap.get(action));
    }

    InvoiceStatus nextState(InvoiceActionTypeEnum action);

    default Boolean availableForAction(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap, InvoiceActionTypeEnum action) {
        return schemaMap.get(action) != null;
    }

    Boolean availableForAction(InvoiceActionTypeEnum action);

    Set<InvoiceActionTypeEnum> getAvailableActionList(InvoiceActionTypeEnum.InvoiceActionParamsValue paramsValue);

    void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap);

    Integer getCode();

    String name();

}
