package me.exrates.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReferralInfoDto {

    private int refId;
    private String email;
    private double refProfitFromUser;
    private int firstRefLevelCount;
    private List<ReferralProfitDto> referralProfitDtoList;
}
