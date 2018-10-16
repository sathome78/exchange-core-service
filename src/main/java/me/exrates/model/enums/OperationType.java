package me.exrates.model.enums;

import me.exrates.exception.UnsupportedOperationTypeException;
import org.springframework.context.MessageSource;

import java.util.*;

import static me.exrates.model.enums.TransactionSourceType.REFILL;
import static me.exrates.model.enums.TransactionSourceType.WITHDRAW;

public enum OperationType {
    INPUT(1, REFILL){{
        /*Addition of three digits is required for IDR input*/
        currencyForAddRandomValueToAmount.put(10, new AdditionalRandomAmountParam(){{
            currencyName = "IDR";
            lowBound = 100;
            highBound = 999;
        }});
    }},
    OUTPUT(2, WITHDRAW),
    SELL(3),
    BUY(4),
    WALLET_INNER_TRANSFER(5),
    REFERRAL(6),
    STORNO(7),
    MANUAL(8),
    USER_TRANSFER(9);

    public class AdditionalRandomAmountParam {
        public String currencyName;
        public double lowBound;
        public double highBound;

    @Override
    public boolean equals(Object currencyName) {
        return this.currencyName.equals((String)currencyName);
    }

    @Override
    public int hashCode() {
        return currencyName != null ? currencyName.hashCode() : 0;
    }
}

    public final int type;

    TransactionSourceType transactionSourceType = null;

    protected final Map<Integer, AdditionalRandomAmountParam> currencyForAddRandomValueToAmount = new HashMap<>();

    OperationType(int type) {
        this.type = type;
    }
    OperationType(int type, TransactionSourceType transactionSourceType) {
        this.type = type;
        this.transactionSourceType = transactionSourceType;
    }

    public Optional<AdditionalRandomAmountParam> getRandomAmountParam(Integer currencyId){
        return Optional.ofNullable(currencyForAddRandomValueToAmount.get(currencyId));
    }

    public Optional<AdditionalRandomAmountParam> getRandomAmountParam(String currencyName){
        return currencyForAddRandomValueToAmount.values().stream()
            .filter(e->e.equals(currencyName))
            .findAny();
    }

    public static List<OperationType> getInputOutputOperationsList(){
        return new ArrayList<OperationType>(){{
            add(INPUT);
            add(OUTPUT);
        }};
    }

    public static OperationType getOpposite(OperationType ot) {
        switch (ot) {
            case INPUT:
                return OUTPUT;
            case OUTPUT:
                return INPUT;
            case SELL:
                return BUY;
            case BUY:
                return SELL;
            default:
                return ot;
        }
    }

    public int getType() {
        return type;
    }

    public TransactionSourceType getTransactionSourceType() {
        return transactionSourceType;
    }

    public static OperationType convert(int id) {
        return Arrays.stream(OperationType.values())
            .filter(operationType -> operationType.type == id)
            .findAny()
            .orElseThrow(() -> new UnsupportedOperationTypeException(id));
    }

    public static OperationType of(String value) {
        return Arrays.stream(OperationType.values())
                .filter(operationType -> operationType.name().equals(value))
                .findAny()
                .orElseThrow(() -> new UnsupportedOperationTypeException("Not supported booking status: " + value));
    }

    public String toString(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage("operationtype." + this.name(), null, locale);
    }
}
