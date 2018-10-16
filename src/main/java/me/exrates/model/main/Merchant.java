package me.exrates.model.main;

import lombok.*;
import me.exrates.model.enums.MerchantProcessType;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Merchant {
    private int id;
    private String name;
    private String description;
    private String serviceBeanName;
    private MerchantProcessType processType;
    private Integer refillOperationCountLimitForUserPerDay;
    private Boolean additionalTagForWithdrawAddressIsUsed;


    public Merchant(int id) {
        this.id = id;
    }

    public Merchant(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}