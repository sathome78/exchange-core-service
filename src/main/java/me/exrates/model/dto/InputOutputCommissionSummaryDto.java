package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
public class InputOutputCommissionSummaryDto {
    private Integer orderNum;
    //wolper 19.04.2018
    //currency id added
    private int curId;
    //wolper 24.04.18
    private BigDecimal rateToUSD;
    private String currencyName;
    private BigDecimal input;
    private BigDecimal output;
    private BigDecimal inputCommission;
    private BigDecimal outputCommission;


    public static String getTitle() {
        return Stream.of("No.", "cur_id", "currency", "rateToUSD", "input", "output", "input_commission", "output_commission")
                .collect(Collectors.joining(";", "", "\r\n"));
    }


    @Override
    public String toString() {
        return Stream.of(String.valueOf(orderNum), String.valueOf(curId), currencyName, BigDecimalProcessing.formatNoneComma(rateToUSD, false),
                BigDecimalProcessing.formatNoneComma(input, false),
                BigDecimalProcessing.formatNoneComma(output, false),
                BigDecimalProcessing.formatNoneComma(inputCommission, false),
                BigDecimalProcessing.formatNoneComma(outputCommission, false))
                .collect(Collectors.joining(";", "", "\r\n"));
    }
}
