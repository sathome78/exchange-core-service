package me.exrates.model.dto;

import lombok.Data;

import java.math.BigDecimal;

import static me.exrates.model.dto.FilterDataItem.DATE_FORMAT;

@Data
public class AdminStopOrderFilterData extends TableFilterData {


    private Integer currencyPairId;
    private Integer orderId;
    private String orderType;
    private String dateFrom;
    private String dateTo;
    private BigDecimal stopRateFrom;
    private BigDecimal stopRateTo;
    private BigDecimal exrateFrom;
    private BigDecimal exrateTo;
    private BigDecimal amountFrom;
    private BigDecimal amountTo;
    private Integer statusId;
    private String creator;



    @Override
    public void initFilterItems() {
        FilterDataItem[] items = new FilterDataItem[] {
                new FilterDataItem("order_id", "STOP_ORDERS.id =", orderId),
                new FilterDataItem("currency_pair_id", "STOP_ORDERS.currency_pair_id =", currencyPairId),
                new FilterDataItem("operation_type_id", "STOP_ORDERS.operation_type_id =", orderType),
                new FilterDataItem("date_from", "STOP_ORDERS.date_creation >=", dateFrom, DATE_FORMAT),
                new FilterDataItem("date_to", "STOP_ORDERS.date_creation <=", dateTo, DATE_FORMAT),
                new FilterDataItem("exrate_from", "STOP_ORDERS.limit_rate >=", exrateFrom),
                new FilterDataItem("exrate_to", "STOP_ORDERS.limit_rate <=", exrateTo),
                new FilterDataItem("exrate_from", "STOP_ORDERS.stop_rate >=", stopRateFrom),
                new FilterDataItem("exrate_to", "STOP_ORDERS.stop_rate <=", stopRateTo),
                new FilterDataItem("amount_base_from", "STOP_ORDERS.amount_base >=", amountFrom),
                new FilterDataItem("amount_base_to", "STOP_ORDERS.amount_base <=", amountTo),
                new FilterDataItem("status_id", "STOP_ORDERS.status_id =", statusId),
                new FilterDataItem("creator_email", "STOP_ORDERS.user_id =", creator, "(SELECT id FROM USER WHERE email = :%s)"),
        };
        populateFilterItemsNonEmpty(items);

    }
}
