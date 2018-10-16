package me.exrates.model.onlineTableDto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import me.exrates.util.LocalDateTimeSerializer;

import java.time.LocalDateTime;

@Setter
@Getter
public class MyReferralDetailedDto extends OnlineTableDto {
    private Integer transactionId;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateTransaction;
    private String amount;
    private String currencyName;
    private Integer referralId;
    private Integer referralLevel;
    private String referralPercent;
    private String initiatorEmail;
    private String status;

    public MyReferralDetailedDto() {
        this.needRefresh = true;
    }

    public MyReferralDetailedDto(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    public int hashCode() {
        int result = transactionId != null ? transactionId.hashCode() : 0;
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        return result;
    }

}
