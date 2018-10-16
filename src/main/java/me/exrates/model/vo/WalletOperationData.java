package me.exrates.model.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.main.Commission;
import me.exrates.model.main.Transaction;

import java.math.BigDecimal;

/**
 * Created by Valk on 24.05.2016.
 */
@Getter
@Setter
@EqualsAndHashCode
public class WalletOperationData {

    private OperationType operationType;
    private int walletId;
    private BigDecimal amount;
    private BalanceType balanceType;
    private Commission commission;
    private BigDecimal commissionAmount;
    private TransactionSourceType sourceType;
    private Integer sourceId;
    private Transaction transaction;
    private String description;

    /**/
    public enum BalanceType {
        ACTIVE,
        RESERVED
    }

    @Override
    public String toString() {
        return "WalletOperationData{" +
                "operationType=" + operationType +
                ", walletId=" + walletId +
                ", amount=" + amount +
                ", balanceType=" + balanceType +
                ", commission=" + commission +
                ", commissionAmount=" + commissionAmount +
                ", sourceType=" + sourceType +
                ", sourceId=" + sourceId +
                ", transaction=" + transaction +
                '}';
    }
}
