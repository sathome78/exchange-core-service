package me.exrates.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

import static me.exrates.model.dto.FilterDataItem.*;


/**
 * Created by OLEG on 28.02.2017.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class RefillFilterData extends TableFilterData {
    private Integer requestId;
    private List<Integer> currencyIds;
    private List<Integer> merchantIds;
    private String startDate;
    private String endDate;
    private BigDecimal amountFrom;
    private BigDecimal amountTo;
    /*private BigDecimal commissionAmountFrom;
    private BigDecimal commissionAmountTo;*/
    private String wallet;
    private String recipientBank;
    private String fullName;
    private String email;
    private String hash;
    private String address;

    @Override
    public void initFilterItems() {
        FilterDataItem[] items = new FilterDataItem[]{
                new FilterDataItem("request_id", "REFILL_REQUEST.id =", requestId),
                new FilterDataItem("currency_ids", "REFILL_REQUEST.currency_id IN", currencyIds, IN_FORMAT),
                new FilterDataItem("merchant_ids", "REFILL_REQUEST.merchant_id IN", merchantIds, IN_FORMAT),
                new FilterDataItem("start_date", "REFILL_REQUEST.date_creation >=", startDate, DATE_FORMAT),
                new FilterDataItem("end_date", "REFILL_REQUEST.date_creation <=", endDate, DATE_FORMAT),
                new FilterDataItem("amount_from", "REFILL_REQUEST.amount >=", amountFrom),
                new FilterDataItem("amount_to", "REFILL_REQUEST.amount <=", amountTo),
                /*  new FilterDataItem("commission_amount_from", "REFILL_REQUEST.commission >=", commissionAmountFrom),
                  new FilterDataItem("commission_amount_to", "REFILL_REQUEST.commission <=", commissionAmountTo),*/
                new FilterDataItem("address", "RRA.address LIKE", address, LIKE_FORMAT_MIDDLE),
                new FilterDataItem("wallet", "IB.account_number LIKE", wallet, LIKE_FORMAT_MIDDLE),
                new FilterDataItem("recipient_bank", "IB.name LIKE", recipientBank, LIKE_FORMAT_MIDDLE),
                new FilterDataItem("tx_hash", "REFILL_REQUEST.merchant_transaction_id LIKE", hash, LIKE_FORMAT_MIDDLE),
                new FilterDataItem("full_name", "RRP.user_full_name LIKE", fullName, LIKE_FORMAT_MIDDLE),
                new FilterDataItem("email", "USER.email LIKE", email, LIKE_FORMAT_MIDDLE)
        };
        populateFilterItemsNonEmpty(items);
    }
}
