package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

import static me.exrates.model.dto.FilterDataItem.DATE_FORMAT;

@Getter
@Setter
@ToString
public class AdminOrderFilterData extends TableFilterData {

    private Integer currencyPairId;
    private Integer orderId;
    private String orderType;
    private String dateFrom;
    private String dateTo;
    private BigDecimal exrateFrom;
    private BigDecimal exrateTo;
    private BigDecimal volumeFrom;
    private BigDecimal volumeTo;
    private Integer statusId;
    private String creator;
    private Integer creatorRole;
    private String acceptor;



    @Override
    public void initFilterItems() {
        FilterDataItem[] items = new FilterDataItem[] {
                new FilterDataItem("order_id", "EXORDERS.id =", orderId),
                new FilterDataItem("currency_pair_id", "EXORDERS.currency_pair_id =", currencyPairId),
                new FilterDataItem("operation_type_id", "EXORDERS.operation_type_id =", orderType),
                new FilterDataItem("date_from", "EXORDERS.date_creation >=", dateFrom, DATE_FORMAT),
                new FilterDataItem("date_to", "EXORDERS.date_creation <=", dateTo, DATE_FORMAT),
                new FilterDataItem("exrate_from", "EXORDERS.exrate >=", exrateFrom),
                new FilterDataItem("exrate_to", "EXORDERS.exrate <=", exrateTo),
                new FilterDataItem("amount_base_from", "EXORDERS.amount_base >=", volumeFrom),
                new FilterDataItem("amount_base_to", "EXORDERS.amount_base <=", volumeTo),
                new FilterDataItem("status_id", "EXORDERS.status_id =", statusId),
                new FilterDataItem("creator_email", "EXORDERS.user_id =", creator, "(SELECT id FROM USER WHERE email = :%s)"),
                new FilterDataItem("creator_role", "CREATOR.roleid =", creatorRole),
                new FilterDataItem("acceptor_email", "EXORDERS.user_acceptor_id =", acceptor, "(SELECT id FROM USER WHERE email = :%s)"),
        };
        populateFilterItemsNonEmpty(items);

    }

}



