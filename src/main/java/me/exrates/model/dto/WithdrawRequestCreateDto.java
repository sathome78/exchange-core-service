package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.WithdrawStatusEnum;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class WithdrawRequestCreateDto {
    private Integer id;
    private Integer userId;
    private String userEmail;
    private Integer userWalletId;
    private Integer currencyId;
    private String currencyName;
    private BigDecimal amount;
    private BigDecimal commission;
    private Integer commissionId;
    private String destinationWallet;
    private String destinationTag;
    private Integer merchantId;
    private String merchantDescription;
    private Integer statusId;
    private String recipientBankName;
    private String recipientBankCode;
    private String userFullName;
    private String remark;
    private Boolean autoEnabled;
    private BigDecimal autoThresholdAmount;
    private BigDecimal merchantCommissionAmount;

    public WithdrawRequestCreateDto(WithdrawRequestParamsDto withdrawRequestParamsDto, CreditsOperation creditsOperation, WithdrawStatusEnum status) {
        this.userId = creditsOperation.getUser().getId();
        this.userEmail = creditsOperation.getUser().getEmail();
        this.userWalletId = creditsOperation.getWallet().getId();
        this.currencyId = creditsOperation.getCurrency().getId();
        this.currencyName = creditsOperation.getCurrency().getName();
        this.amount = creditsOperation.getOrigAmountAtCreationRequest();
        this.commission = creditsOperation.getCommissionAmount();
        this.commissionId = creditsOperation.getCommission().getId();
        this.destinationWallet = creditsOperation.getDestination().orElse(null);
        this.destinationTag = creditsOperation.getDestinationTag().orElse(null);
        this.merchantId = creditsOperation.getMerchant().getId();
        this.merchantDescription = creditsOperation.getMerchant().getDescription();
        this.statusId = status.getCode();
        this.recipientBankName = withdrawRequestParamsDto.getRecipientBankName();
        this.recipientBankCode = withdrawRequestParamsDto.getRecipientBankCode();
        this.userFullName = withdrawRequestParamsDto.getUserFullName();
        this.remark = withdrawRequestParamsDto.getRemark();
        this.autoEnabled = null;
        this.autoThresholdAmount = null;
        this.merchantCommissionAmount = creditsOperation.getMerchantCommissionAmount();
    }
}
