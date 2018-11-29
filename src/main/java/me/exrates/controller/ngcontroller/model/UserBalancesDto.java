package me.exrates.controller.ngcontroller.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@Builder
public class UserBalancesDto implements RowMapper<UserBalancesDto> {

    private String coinImageUrl = "";
    private Integer currencyId;
    private String coinName;
    private String coinDescription;
    private String processType;
    private String merchantDescription;
    private String description;
    private BigDecimal availableBalance;
    private BigDecimal reservedBalance;
    private BigDecimal usdRate;
    private BigDecimal availableBalanceInUSD;
    private BigDecimal reservedBalanceInUSD;
    private BigDecimal chartChanges;


    @Override
    public UserBalancesDto mapRow(ResultSet resultSet, int i) throws SQLException {

        return UserBalancesDto.
                builder()
                .currencyId(resultSet.getInt("currency_id"))
                .availableBalance(resultSet.getBigDecimal("active_balance"))
                .reservedBalance(resultSet.getBigDecimal("reserved_balance"))
                .coinName(resultSet.getString("name"))
                .coinDescription(resultSet.getString("description"))
                .usdRate(resultSet.getBigDecimal("usd_rate"))
                .availableBalance(resultSet.getBigDecimal("active_balance_in_usd"))
                .reservedBalanceInUSD(resultSet.getBigDecimal("reserved_balance_in_usd"))
                .description(resultSet.getString("description"))
                .processType(resultSet.getString("process_type"))
                .build();
    }

}
