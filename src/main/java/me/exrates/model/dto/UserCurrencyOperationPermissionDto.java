package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.InvoiceOperationDirection;
import me.exrates.model.enums.InvoiceOperationPermission;

@Getter
@Setter
@ToString
public class UserCurrencyOperationPermissionDto {

    private Integer userId;
    private Integer currencyId;
    private String currencyName;
    private InvoiceOperationDirection invoiceOperationDirection;
    private InvoiceOperationPermission invoiceOperationPermission;
}
