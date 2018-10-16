package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.RefillStatusEnum;
import me.exrates.model.enums.TransactionSourceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class RefillRequestFlatForReportDto {
  private int id;
  private String address;
  private String recipientBankName;
  private String userFullName;
  private RefillStatusEnum status;
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
