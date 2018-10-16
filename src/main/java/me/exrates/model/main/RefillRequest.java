package me.exrates.model.main;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.RefillStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by ValkSam
 */
@Getter
@Setter
public class RefillRequest {
  private int id;
  private String address;
  private Integer userId;
  private String payerBankName;
  private String payerBankCode;
  private String payerAccount;
  private String userFullName;
  private String remark;
  private String receiptScan;
  private String receiptScanName;
  private BigDecimal amount;
  private Integer commissionId;
  private RefillStatusEnum status;
  private LocalDateTime dateCreation;
  private LocalDateTime statusModificationDate;
  private Integer currencyId;
  private Integer merchantId;
  private String merchantTransactionId;
  private String recipientBankName;
  private Integer recipientBankId;
  private String recipientBankAccount;
  private String recipientBankRecipient;
  private Integer adminHolderId;
  private Integer confirmations;
}
