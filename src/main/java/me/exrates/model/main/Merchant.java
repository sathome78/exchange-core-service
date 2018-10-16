package me.exrates.model.main;

import lombok.*;
import me.exrates.model.enums.MerchantProcessType;

@Data
public class Merchant {
    private int id;
    private String name;
    private String description;
    private String serviceBeanName;
    private MerchantProcessType processType;
    private Integer refillOperationCountLimitForUserPerDay;
    private Boolean additionalTagForWithdrawAddressIsUsed;
}