package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.ActionType;
import me.exrates.util.BigDecimalProcessing;

import java.math.BigDecimal;

/**
 * Created by Valk on 04.05.2016.
 * <p/>
 * class is used for upload data
 */

@Getter
@Setter
public class UserSummaryDto {
  private String userNickname;
  private String userEmail;
  private String creationDate;
  private String registeredIp;
  private String lastIp;
  private String currencyName;
  private BigDecimal activeBalance;
  private BigDecimal reservedBalance;
  private BigDecimal inputSummary;
  private BigDecimal outputSummary;
  private Boolean bothCurrencyPermissionsPresent;

  public Boolean isEmpty() {
    return (activeBalance == null || activeBalance.compareTo(BigDecimal.ZERO) == 0) &&
        (reservedBalance == null || reservedBalance.compareTo(BigDecimal.ZERO) == 0) &&
        (inputSummary == null || inputSummary.compareTo(BigDecimal.ZERO) == 0) &&
        (outputSummary == null || outputSummary.compareTo(BigDecimal.ZERO) == 0);
  }

  public static String getTitle() {
    return "Name" + ";" +
        "Email" + ";" +
        "Creation date" + ";" +
        "IP" + ";" +
        "Last IP" + ";" +
        "Wallet" + ";" +
        "balance" + ";" +
        "reserve" + ";" +
        "input summary" + ";" +
        "output summary" + ";" +
        "turnover" +
        "\r\n";
  }

  @Override
  public String toString() {
    return userNickname + ";" +
        userEmail + ";" +
        creationDate + ";" +
        (registeredIp == null ? "" : registeredIp) + ";" +
        (lastIp == null ? "" : lastIp) + ";" +
        (currencyName == null ? "" : currencyName) + ";" +
        BigDecimalProcessing.formatNoneComma(activeBalance, false) + ";" +
        BigDecimalProcessing.formatNoneComma(reservedBalance, false) + ";" +
        BigDecimalProcessing.formatNoneComma(inputSummary, false) + ";" +
        BigDecimalProcessing.formatNoneComma(outputSummary, false) + ";" +
        (bothCurrencyPermissionsPresent ?
            BigDecimalProcessing.formatNoneComma(
                BigDecimalProcessing.doActionLax(inputSummary, outputSummary, ActionType.SUBTRACT), false) :
            "Not enough rights") +
        "\r\n";
  }

}
