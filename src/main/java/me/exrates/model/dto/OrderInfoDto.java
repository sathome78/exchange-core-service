package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.util.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Valk on 11.05.2016.
 */
@Getter
@Setter
@ToString
public class OrderInfoDto {
    private int id;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateCreation;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateAcception;
    private String currencyPairName;
    private String orderTypeName;
    private String orderStatusName;
    private String exrate;
    private String stopRate;
    private String amountBase;
    private String amountConvert;
    private String currencyBaseName;
    private String currencyConvertName;
    private String orderCreatorEmail;
    private String orderAcceptorEmail;
    private String transactionCount;
    private String companyCommission;
    private Integer source;
    private List<Integer> children;
};

