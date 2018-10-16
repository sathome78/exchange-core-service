package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
public class CurrencyInputOutputSummaryDto {
    private Integer orderNum;
    private int curId;
    private BigDecimal rateToUSD;
    private String currencyName;
    private BigDecimal input;
    private BigDecimal output;
    private BigDecimal diff;

    public void calculateAndSetDiff() {
        diff = input.subtract(output);
    }

    public static String getTitle() {
        return Stream.of("No.", "cur_id", "currency", "rateToUSD", "input", "output", "diff")
                .collect(Collectors.joining(";", "", "\r\n"));
    }


    @Override
    public String toString() {
        return Stream.of(String.valueOf(orderNum), String.valueOf(curId), currencyName, BigDecimalProcessing.formatNoneComma(rateToUSD, false),
                BigDecimalProcessing.formatNoneComma(input, false),
                BigDecimalProcessing.formatNoneComma(output, false),
                BigDecimalProcessing.formatNoneComma(diff, false))
                .collect(Collectors.joining(";", "", "\r\n"));
    }
}
