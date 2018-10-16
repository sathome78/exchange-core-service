package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.InvoiceStatus;
import me.exrates.util.BigDecimalProcessing;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.OperationType.OUTPUT;

@Getter
@Setter
public class InvoiceReportDto {
  private Integer docId;
  private String currency;
  private String creationDate;
  private String userNickname;
  private String userEmail;
  private String recipientBank;
  private BigDecimal amount;
  private String payerName;
  private String payerBankCode;
  private String status;
  private String acceptorUserEmail;
  private String acceptanceDate;
  private String system;
  private String operation;
  private String merchant;
  private InvoiceStatus statusEnum;
  //wolper 24.04.18
  private BigDecimal rateToUSD;


  public InvoiceReportDto(TransactionFlatForReportDto transactionFlatForReportDto) {
    this.docId = transactionFlatForReportDto.getTransactionId();
    this.currency = transactionFlatForReportDto.getCurrency();
    this.creationDate = transactionFlatForReportDto.getDatetime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    this.userNickname = transactionFlatForReportDto.getUserNickname();
    this.userEmail = transactionFlatForReportDto.getUserEmail();
    this.recipientBank = "";
    this.amount = transactionFlatForReportDto.getAmount();
    this.payerName = transactionFlatForReportDto.getMerchant();
    this.payerBankCode = transactionFlatForReportDto.getMerchant();
    this.status = transactionFlatForReportDto.getProvided() ? "PROVIDED" : "WAITING_FOR_PROVIDING";
    this.statusEnum = null;
    this.acceptorUserEmail = "";
    this.acceptanceDate = transactionFlatForReportDto.getProvidedDate() == null ? "" : transactionFlatForReportDto.getProvidedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    this.operation = transactionFlatForReportDto.getOperationType().name();
    this.system = transactionFlatForReportDto.getSourceType().name();
    this.merchant = transactionFlatForReportDto.getMerchant();
  }

  public InvoiceReportDto(WithdrawRequestFlatForReportDto withdrawRequestFlatForReportDto) {
    this.docId = withdrawRequestFlatForReportDto.getInvoiceId();
    this.currency = withdrawRequestFlatForReportDto.getCurrency();
    this.creationDate = withdrawRequestFlatForReportDto.getDatetime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    this.userNickname = withdrawRequestFlatForReportDto.getUserNickname();
    this.userEmail = withdrawRequestFlatForReportDto.getUserEmail();
    this.recipientBank = !StringUtils.isEmpty(withdrawRequestFlatForReportDto.getRecipientBank()) ?
        withdrawRequestFlatForReportDto.getRecipientBank() :
        StringUtils.isEmpty(withdrawRequestFlatForReportDto.getWallet()) ?
            "" :
            withdrawRequestFlatForReportDto.getWallet();
    this.amount = withdrawRequestFlatForReportDto.getAmount();
    this.payerName = withdrawRequestFlatForReportDto.getMerchant();
    this.payerBankCode = withdrawRequestFlatForReportDto.getMerchant();
    this.status = withdrawRequestFlatForReportDto.getStatus().name();
    this.statusEnum = withdrawRequestFlatForReportDto.getStatus();
    this.acceptorUserEmail = withdrawRequestFlatForReportDto.getAdminEmail();
    this.acceptanceDate = withdrawRequestFlatForReportDto.getAcceptanceTime() == null ? "" : withdrawRequestFlatForReportDto.getAcceptanceTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    this.operation = OUTPUT.name();
    this.system = withdrawRequestFlatForReportDto.getSourceType().name();
    this.merchant = withdrawRequestFlatForReportDto.getMerchant();
  }

  public InvoiceReportDto(RefillRequestFlatForReportDto refillRequestFlatForReportDto) {
    this.docId = refillRequestFlatForReportDto.getId();
    this.currency = refillRequestFlatForReportDto.getCurrency();
    this.creationDate = refillRequestFlatForReportDto.getDatetime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    this.userNickname = refillRequestFlatForReportDto.getUserNickname();
    this.userEmail = refillRequestFlatForReportDto.getUserEmail();
    this.recipientBank = !StringUtils.isEmpty(refillRequestFlatForReportDto.getRecipientBankName()) ?
        refillRequestFlatForReportDto.getRecipientBankName() :
        StringUtils.isEmpty(refillRequestFlatForReportDto.getAddress()) ?
            "" :
            refillRequestFlatForReportDto.getAddress();
    this.amount = refillRequestFlatForReportDto.getAmount();
    this.payerName = refillRequestFlatForReportDto.getMerchant();
    this.payerBankCode = refillRequestFlatForReportDto.getMerchant();
    this.status = refillRequestFlatForReportDto.getStatus().name();
    this.statusEnum = refillRequestFlatForReportDto.getStatus();
    this.acceptorUserEmail = refillRequestFlatForReportDto.getAdminEmail();
    this.acceptanceDate = refillRequestFlatForReportDto.getAcceptanceTime() == null ? "" : refillRequestFlatForReportDto.getAcceptanceTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    this.operation = INPUT.name();
    this.system = refillRequestFlatForReportDto.getSourceType().name();
    this.merchant = refillRequestFlatForReportDto.getMerchant();
  }

  public static String getTitle() {
    return "doc id" + ";" +
        "Currency" + ";" +
            "rateToUSD" + ";" +
        "Creation date" + ";" +
        "Email" + ";" +
        "Recipient bank / Blockchain address" + ";" +
        "Amount" + ";" +
        "Payer name" + ";" +
        "Payer bank" + ";" +
        "status" + ";" +
        "Acceptor's Email" + ";" +
        "Acceptance/Change status date" + ";" +
        "Operation" + ";" +
        "System" +
        "\r\n";
  }

  @Override
  public String toString() {
    return docId + ";" +
        currency + ";" +
            BigDecimalProcessing.formatNoneComma(rateToUSD, false) + ";" +
        creationDate + ";" +
        userEmail + ";" +
        recipientBank + ";" +
        BigDecimalProcessing.formatNoneComma(amount, false) + ";" +
        (payerName == null ? "" : payerName) + ";" +
        (payerBankCode == null ? "" : payerBankCode) + ";" +
        status + ";" +
        (acceptorUserEmail == null ? "" : acceptorUserEmail) + ";" +
        (acceptanceDate == null ? "" : acceptanceDate) + ";" +
        (operation == null ? "" : operation) + ";" +
        (system == null ? "" : system) +
        "\r\n";
  }

}
