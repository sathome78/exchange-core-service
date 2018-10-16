package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.OperationType;
import me.exrates.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
public class OrdersCommissionSummaryDto {
    private Integer orderNum;
    private int pairId;
    private String currencyPairName;
    private String currencyAccountingName;
    private OperationType operationType;
    private BigDecimal amountBase;
    private BigDecimal amountConvert;
    private BigDecimal commissionAmount;


    public static String getTitle() {
        return Stream.of("No.", "pair_id", "currency_pair", "currency_accounting", "operation_type", "amount_base", "amount_convert", "commission_amount")
                .collect(Collectors.joining(";", "", "\r\n"));
    }


    @Override
    public String toString() {
        return Stream.of(String.valueOf(orderNum), String.valueOf(pairId), currencyPairName, currencyAccountingName, operationType.name(),
                BigDecimalProcessing.formatNoneComma(amountBase, false),
                BigDecimalProcessing.formatNoneComma(amountConvert, false),
                BigDecimalProcessing.formatNoneComma(commissionAmount, false))
                .collect(Collectors.joining(";", "", "\r\n"));
    }
}
