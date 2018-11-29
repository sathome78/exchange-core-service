package me.exrates.controller.ngcontroller.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


@Data
@Builder
public class RefillPendingRequestDto implements RowMapper<RefillPendingRequestDto> {

    private String date;
    private String currency;
    private double amount;
    private double commission;
    private String system;
    private String status;


    @Override
    public RefillPendingRequestDto mapRow(ResultSet rs, int rowNum) throws SQLException {

        return RefillPendingRequestDto.builder()
                .date(rs.getString("date"))
                .currency(rs.getString("currency"))
                .amount(rs.getDouble("amount"))
                .commission(rs.getDouble("commission"))
                .system(rs.getString("system"))
                .status(rs.getString("status"))
                .build();
    }
}
