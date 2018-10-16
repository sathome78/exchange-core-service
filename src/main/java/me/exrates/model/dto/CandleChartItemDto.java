package me.exrates.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class CandleChartItemDto {

    private LocalDateTime beginPeriod;
    private LocalDateTime endPeriod;
    private BigDecimal openRate;
    private BigDecimal closeRate;
    private BigDecimal lowRate;
    private BigDecimal highRate;
    private BigDecimal baseVolume;
    private Timestamp beginDate;
    private Timestamp endDate;
}
