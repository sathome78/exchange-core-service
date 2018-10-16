package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.OperationType;

import java.math.BigDecimal;

/**
 * @author ValkSam
 */
@Getter
@Setter
@ToString
public class WithdrawRequestParamsDto {
    private Integer currency;
    private Integer merchant;
    private BigDecimal sum;
    private String destination;
    private String destinationTag;
    private int merchantImage;
    private OperationType operationType;
    private String recipientBankName;
    private String recipientBankCode;
    private String userFullName;
    private String remark;
    private String walletNumber;
}
