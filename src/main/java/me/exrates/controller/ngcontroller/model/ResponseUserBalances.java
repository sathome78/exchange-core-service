package me.exrates.controller.ngcontroller.model;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class ResponseUserBalances {

    private BigDecimal balanceByCurrency1;
    private BigDecimal balanceByCurrency2;
}
