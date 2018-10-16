package me.exrates.model.main;

import lombok.*;
import me.exrates.model.dto.WithdrawRequestCreateDto;
import me.exrates.model.enums.WithdrawStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
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

    public WithdrawRequest(WithdrawRequestCreateDto withdrawRequestCreateDto) {
        this.id = withdrawRequestCreateDto.getId();
        this.wallet = withdrawRequestCreateDto.getDestinationWallet();
        this.destinationTag = withdrawRequestCreateDto.getDestinationTag();
        this.userId = withdrawRequestCreateDto.getUserId();
        this.userEmail = withdrawRequestCreateDto.getUserEmail();
        this.recipientBankName = withdrawRequestCreateDto.getRecipientBankName();
        this.recipientBankCode = withdrawRequestCreateDto.getRecipientBankCode();
        this.userFullName = withdrawRequestCreateDto.getUserFullName();
        this.remark = withdrawRequestCreateDto.getRemark();
        this.amount = withdrawRequestCreateDto.getAmount();
        this.commissionAmount = withdrawRequestCreateDto.getCommission();
        this.status = WithdrawStatusEnum.convert(withdrawRequestCreateDto.getStatusId());
    }

}
