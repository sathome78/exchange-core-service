package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyWalletConfirmationDetailDto {
    private String amount;
    private String commission;
    private String total;
    private String stage;
}
