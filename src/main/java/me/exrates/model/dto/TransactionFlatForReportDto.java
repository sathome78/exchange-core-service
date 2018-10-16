package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by ValkSam
 */
@Getter
@Setter
public class TransactionFlatForReportDto {
    private int transactionId;

    private String merchant;
    private LocalDateTime providedDate;

    private String userNickname;
    private String userEmail;

    private BigDecimal amount;
    private BigDecimal commissionAmount;
    private LocalDateTime datetime;
    private Integer confirmation;
    private Boolean provided;
    private TransactionSourceType sourceType;

    private String currency;
    private OperationType operationType;
}
