package me.exrates.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

import static me.exrates.model.dto.FilterDataItem.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class WithdrawFilterData extends TableFilterData {
    private Integer requestId;
    private List<Integer> currencyIds;
    private List<Integer> merchantIds;
    private String startDate;
    private String endDate;
    private String startDateStatus;
    private String endDateStatus;
    private BigDecimal amountFrom;
    private BigDecimal amountTo;
    private BigDecimal commissionAmountFrom;
    private BigDecimal commissionAmountTo;
    private String wallet;
    private String recipientBank;
    private String fullName;
    private String email;

    @Override
    public void initFilterItems() {
        FilterDataItem[] items = new FilterDataItem[]{
                new FilterDataItem("request_id", "WITHDRAW_REQUEST.id =", requestId),
                new FilterDataItem("currency_ids", "WITHDRAW_REQUEST.currency_id IN", currencyIds, IN_FORMAT),
                new FilterDataItem("merchant_ids", "WITHDRAW_REQUEST.merchant_id IN", merchantIds, IN_FORMAT),
                new FilterDataItem("start_date", "WITHDRAW_REQUEST.date_creation >=", startDate, DATE_FORMAT),
                new FilterDataItem("end_date", "WITHDRAW_REQUEST.date_creation <=", endDate, DATE_FORMAT),
                new FilterDataItem("start_date_status", "WITHDRAW_REQUEST.status_modification_date >=", startDateStatus, DATE_FORMAT),
                new FilterDataItem("end_date_status", "WITHDRAW_REQUEST.status_modification_date <=", endDateStatus, DATE_FORMAT),
                new FilterDataItem("amount_from", "WITHDRAW_REQUEST.amount >=", amountFrom),
                new FilterDataItem("amount_to", "WITHDRAW_REQUEST.amount <=", amountTo),
                new FilterDataItem("commission_amount_from", "WITHDRAW_REQUEST.commission >=", commissionAmountFrom),
                new FilterDataItem("commission_amount_to", "WITHDRAW_REQUEST.commission <=", commissionAmountTo),
                new FilterDataItem("wallet", "WITHDRAW_REQUEST.wallet LIKE", wallet, LIKE_FORMAT_MIDDLE),
                new FilterDataItem("recipient_bank", "WITHDRAW_REQUEST.recipient_bank_name LIKE", recipientBank, LIKE_FORMAT_MIDDLE),
                new FilterDataItem("full_name", "WITHDRAW_REQUEST.user_full_name LIKE", fullName, LIKE_FORMAT_MIDDLE),
                new FilterDataItem("email", "USER.email LIKE", email, LIKE_FORMAT_MIDDLE)
        };
        populateFilterItemsNonEmpty(items);
    }
}
