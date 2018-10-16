package me.exrates.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

import static me.exrates.model.dto.FilterDataItem.*;

@Data
public class VoucherFilterData extends TableFilterData {

    private Integer voucherId;
    private List<Integer> currencyIds;
    private List<Integer> merchantIds;
    private String startDate;
    private String endDate;
    private BigDecimal amountFrom;
    private BigDecimal amountTo;
    private BigDecimal commissionAmountFrom;
    private BigDecimal commissionAmountTo;
    private String creatorEmail;
    private String recipientEmail;
    private List<Integer> statuses;
    private String hash;

    @Override
    public void initFilterItems() {
        FilterDataItem[] items = new FilterDataItem[]{
                new FilterDataItem("request_id", "TRANSFER_REQUEST.id =", voucherId),
                new FilterDataItem("currency_ids", "TRANSFER_REQUEST.currency_id IN", currencyIds, IN_FORMAT),
                new FilterDataItem("merchant_ids", "TRANSFER_REQUEST.merchant_id IN", merchantIds, IN_FORMAT),
                new FilterDataItem("start_date", "TRANSFER_REQUEST.date_creation >=", startDate, DATE_FORMAT),
                new FilterDataItem("end_date", "TRANSFER_REQUEST.date_creation <=", endDate, DATE_FORMAT),
                new FilterDataItem("amount_from", "TRANSFER_REQUEST.amount >=", amountFrom),
                new FilterDataItem("amount_to", "TRANSFER_REQUEST.amount <=", amountTo),
                new FilterDataItem("commission_amount_from", "TRANSFER_REQUEST.commission >=", commissionAmountFrom),
                new FilterDataItem("commission_amount_to", "TRANSFER_REQUEST.commission <=", commissionAmountTo),
                new FilterDataItem("hash", "CONVERT(TRANSFER_REQUEST.hash USING utf8) LIKE", hash, LIKE_FORMAT_MIDDLE),
                new FilterDataItem("statuses", "TRANSFER_REQUEST.status_id IN", statuses, IN_FORMAT),
                new FilterDataItem("recipientEmail", "CONVERT(UR.email USING utf8) LIKE", recipientEmail, LIKE_FORMAT_MIDDLE),
                new FilterDataItem("creatorEmail", "CONVERT(UC.email USING utf8) LIKE", creatorEmail, LIKE_FORMAT_MIDDLE)
        };
        populateFilterItemsNonEmpty(items);
    }
}
