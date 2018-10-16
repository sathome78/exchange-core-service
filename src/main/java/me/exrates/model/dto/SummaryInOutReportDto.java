package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.exception.UnsupportedOperationTypeException;
import me.exrates.model.enums.OperationType;
import me.exrates.util.BigDecimalProcessing;

import java.math.BigDecimal;

import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.OperationType.OUTPUT;

@Getter
@Setter
public class SummaryInOutReportDto {
  private Integer docId;
  private String currency;
  private String userNickname;
  private String userName;
  private String userEmail;
  private String creationDateIn = "";
  private String acceptanceDateIn = "";
  private String creationDateOut = "";
  private String acceptanceDateOut = "";
  private BigDecimal amount;
  private String system;
  private String merchant;
  //wolper 24.04.18
  private BigDecimal rateToUSD;

  public SummaryInOutReportDto(InvoiceReportDto invoiceReportDto) {
    this.docId = invoiceReportDto.getDocId();
    this.currency = invoiceReportDto.getCurrency();
    this.userNickname = invoiceReportDto.getUserNickname();
    this.userEmail = invoiceReportDto.getUserEmail();
    OperationType operationType = OperationType.valueOf(invoiceReportDto.getOperation());
    if (operationType == INPUT) {
      this.creationDateIn = invoiceReportDto.getCreationDate();
      this.acceptanceDateIn = invoiceReportDto.getAcceptanceDate();
    } else if (operationType == OUTPUT) {
      this.creationDateOut = invoiceReportDto.getCreationDate();
      this.acceptanceDateOut = invoiceReportDto.getAcceptanceDate();
    } else {
      throw new UnsupportedOperationTypeException(operationType.name());
    }
    this.amount = invoiceReportDto.getAmount();
    this.system = invoiceReportDto.getSystem();
    this.merchant = invoiceReportDto.getMerchant();
    this.rateToUSD=invoiceReportDto.getRateToUSD();
  }


  public static String getTitle() {
    return "Name" + ";" +
        "Email" + ";" +
        "Creation In" + ";" +
        "Confirmation In" + ";" +
        "Creation Out" + ";" +
        "Confirmation Out" + ";" +
        "Merchant" + ";" +
        "Currency" + ";" +
            "rateToUSD" + ";" +
        "Amount" + ";" +
        "doc_id" +
        "\r\n";
  }

  @Override
  public String toString() {
    return userNickname + ";" +
        userEmail + ";" +
        creationDateIn + ";" +
        acceptanceDateIn + ";" +
        creationDateOut + ";" +
        acceptanceDateOut + ";" +
        merchant + ";" +
        currency + ";" +
            BigDecimalProcessing.formatNoneComma(rateToUSD, false)+ ";" +
        BigDecimalProcessing.formatNoneComma(amount, false) + ";" +
        docId +
        "\r\n";
  }

}
