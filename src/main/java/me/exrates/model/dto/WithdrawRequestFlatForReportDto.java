package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.WithdrawStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class WithdrawRequestFlatForReportDto {
    private int invoiceId;
    private String wallet;
    private String recipientBank;
    private String userFullName;
    private WithdrawStatusEnum status;
    private LocalDateTime acceptanceTime;

    private String userNickname;
    private String userEmail;
    private String adminEmail;

    private BigDecimal amount;
    private BigDecimal commissionAmount;
    private LocalDateTime datetime;
    private String merchant;

    private String currency;
    private TransactionSourceType sourceType;
}
