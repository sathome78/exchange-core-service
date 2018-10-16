package me.exrates.model.enums;


import me.exrates.exception.TransactionLabelTypeAmountParamNeededException;
import me.exrates.exception.TransactionLabelTypeMoreThenOneResultException;
import me.exrates.exception.TransactionLabelTypeNotResolvedException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static me.exrates.model.enums.TransactionSourceType.*;

public enum TransactionType {
    REFILL_IN(REFILL, null),
    WITHDRAW_OUT(WITHDRAW, OperationType.OUTPUT),
    ORDER_IN(ORDER, OperationType.INPUT),
    ORDER_OUT(ORDER, OperationType.OUTPUT),
    RESERVE_TO(null, OperationType.WALLET_INNER_TRANSFER, (v) -> v.compareTo(BigDecimal.ZERO) < 0),
    RESERVE_FROM(null, OperationType.WALLET_INNER_TRANSFER, (v) -> v.compareTo(BigDecimal.ZERO) >= 0),
    REFERRAL(TransactionSourceType.REFERRAL, null),
    MANUAL(TransactionSourceType.MANUAL, null),
    USER_TRANSFER_IN(USER_TRANSFER, OperationType.INPUT),
    USER_TRANSFER_OUT(USER_TRANSFER, OperationType.OUTPUT),
    NOTIFICATIONS(TransactionSourceType.NOTIFICATIONS, null);

    private TransactionSourceType sourceType;
    private OperationType operationType;
    private Predicate<BigDecimal> amountPredicate = null;

    TransactionType(TransactionSourceType sourceType, OperationType operationType) {
        this.sourceType = sourceType;
        this.operationType = operationType;
    }

    TransactionType(TransactionSourceType sourceType, OperationType operationType, Predicate<BigDecimal> amountPredicate) {
        this.sourceType = sourceType;
        this.operationType = operationType;
        this.amountPredicate = amountPredicate;
    }

    public TransactionSourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(TransactionSourceType sourceType) {
        this.sourceType = sourceType;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public Predicate<BigDecimal> getAmountPredicate() {
        return amountPredicate;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public static TransactionType resolveFromOperationTypeAndSource(TransactionSourceType sourceType, OperationType operationType) {
        List<TransactionType> candidates = Arrays.stream(TransactionType.class.getEnumConstants())
                .filter(e -> (e.sourceType == null || e.sourceType == sourceType) && ((e.operationType == null) || (e.operationType == operationType)))
                .collect(Collectors.toList());
        if (candidates.isEmpty()) {
            throw new TransactionLabelTypeNotResolvedException(String.format("sourceType: %s operationType: %s", sourceType, operationType));
        }
        if (candidates.size() > 1) {
            throw new TransactionLabelTypeAmountParamNeededException(String.format("sourceType: %s operationType: %s", sourceType, operationType));
        }
        return candidates.get(0);
    }

    public static TransactionType resolveFromOperationTypeAndSource(TransactionSourceType sourceType, OperationType operationType, BigDecimal amount) {
        List<TransactionType> candidates = Arrays.stream(TransactionType.class.getEnumConstants())
                .filter(e -> (e.sourceType == null || e.sourceType == sourceType) && ((e.operationType == null) || (e.operationType == operationType)))
                .filter(e -> e.amountPredicate == null || e.amountPredicate.test(amount))
                .collect(Collectors.toList());
        if (candidates.isEmpty()) {
            throw new TransactionLabelTypeNotResolvedException(String.format("sourceType: %s operationType: %s", sourceType, operationType));
        }
        if (candidates.size() > 1) {
            candidates = candidates.stream()
                    .filter(e -> e.amountPredicate != null && e.amountPredicate.test(amount))
                    .collect(Collectors.toList());
            if (candidates.size() > 1) {
                throw new TransactionLabelTypeMoreThenOneResultException(String.format("sourceType: %s operationType: %s amount: %s", sourceType, operationType, amount));
            }
        }
        return candidates.get(0);
    }

    @Override
    public String toString() {
        return this.name();
    }
}
