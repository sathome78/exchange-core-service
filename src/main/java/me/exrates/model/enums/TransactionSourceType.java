package me.exrates.model.enums;

import lombok.Getter;
import me.exrates.exception.UnsupportedTransactionSourceTypeIdException;
import me.exrates.exception.UnsupportedTransactionSourceTypeNameException;
import org.springframework.context.MessageSource;

import java.util.Arrays;
import java.util.Locale;

/**
 * Created by Valk on 23.05.2016.
 */
@Getter
public enum TransactionSourceType {
  ORDER(1),
  REFERRAL(3),
  ACCRUAL(4),
  MANUAL(5),
  USER_TRANSFER(6),
  WITHDRAW(9),
  STOP_ORDER(10),
  REFILL(11),
  NOTIFICATIONS(12);

  private final int code;

  TransactionSourceType(int code) {
    this.code = code;
  }

  public static TransactionSourceType convert(int id) {
    return Arrays.stream(TransactionSourceType.class.getEnumConstants())
        .filter(e -> e.code == id)
        .findAny()
        .orElseThrow(() -> new UnsupportedTransactionSourceTypeIdException(String.valueOf(id)));
  }

  public static TransactionSourceType convert(String name) {
    return Arrays.stream(TransactionSourceType.class.getEnumConstants())
        .filter(e -> e.name().equals(name))
        .findAny()
        .orElseThrow(() -> new UnsupportedTransactionSourceTypeNameException(name));
  }

  public String toString(MessageSource messageSource, Locale locale) {
    return messageSource.getMessage("transactionsourcetype." + this.name(), null, locale);

  }
}
