package me.exrates.model.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by maks on 05.04.2017.
 */
@Data
public class ReferralInfoDto {

    private int refId;
    private String email;
    private double refProfitFromUser;
    private int firstRefLevelCount;
    private  List<ReferralProfitDto> referralProfitDtoList;
}
