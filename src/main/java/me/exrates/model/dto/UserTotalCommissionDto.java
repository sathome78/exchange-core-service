package me.exrates.model.dto;


import lombok.Getter;
import lombok.Setter;
import me.exrates.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
public class UserTotalCommissionDto {
    private Integer refillNum;
    private String currencyName;
    private String email;
    private BigDecimal orderCommissiom;
    private BigDecimal withdrawCommission;

    public static String getTitle() {
        return Stream.of("No.", "currency_name", "user_email", "order_commission", "withdraw_commission")
                .collect(Collectors.joining(";", "", "\r\n"));
    }

    @Override
    public String toString() {
        return Stream.of(
                String.valueOf(refillNum),
                currencyName,
                email,
                BigDecimalProcessing.formatNoneComma(orderCommissiom, false),
                BigDecimalProcessing.formatNoneComma(withdrawCommission, false)
        ).collect(Collectors.joining(";", "", "\r\n"));
    }
}
