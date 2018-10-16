package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.TransactionType;
import me.exrates.model.main.ExOrder;
import me.exrates.model.main.Merchant;
import me.exrates.util.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class OperationViewDto {
    private BigDecimal amount;
    private BigDecimal amountBuy;
    private BigDecimal commissionAmount;
    private TransactionType operationType;
    private String currency;
    private Merchant merchant;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime datetime;
    private ExOrder order;
    private String status;
    private String SourceType;
    private Integer SourceId;

    public static String getTitle() {
        return "Date;" +
                "Operation Type;" +
                "Status;" +
                "Currency;" +
                "Amount;" +
                "Commission;" +
                "Merchant;" +
                "Source Id" +
                "\r\n";
    }

    @Override
    public String toString() {
        return datetime + ";" +
                operationType + ";" +
                status + ";" +
                currency + ";" +
                amount + ";" +
                commissionAmount + ";" +
                merchant + ";" +
                SourceId + ";" +
                "\r\n";
    }
}
