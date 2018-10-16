package me.exrates.model.main;

import lombok.Data;
import me.exrates.model.enums.WithdrawStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WithdrawRequest {
    private Integer id;
    private String wallet;
    private String destinationTag;
    private Integer userId;
    private String userEmail;
    private String recipientBankName;
    private String recipientBankCode;
    private String userFullName;
    private String remark;
    private BigDecimal amount;
    private BigDecimal commissionAmount;
    private Integer commissionId;
    private WithdrawStatusEnum status;
    private LocalDateTime dateCreation;
    private LocalDateTime statusModificationDate;
    private Currency currency;
    private Merchant merchant;
    private Integer adminHolderId;
}
