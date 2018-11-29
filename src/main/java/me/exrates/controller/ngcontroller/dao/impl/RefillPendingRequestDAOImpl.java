package me.exrates.controller.ngcontroller.dao.impl;

import me.exrates.controller.ngcontroller.dao.RefillPendingRequestDAO;
import me.exrates.controller.ngcontroller.model.RefillPendingRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RefillPendingRequestDAOImpl implements RefillPendingRequestDAO {

    private static final String GET_PENDING_REQUESTS = "SELECT rr.date_creation as date, m.name as currency, rr.amount, stat.name as status, com.value as commission, m.description as system FROM REFILL_REQUEST as rr\n" +
            "                                                JOIN COMMISSION as com ON rr.commission_id = com.id\n" +
            "                                                JOIN REFILL_REQUEST_STATUS stat on rr.status_id = stat.id\n" +
            "                                                JOIN MERCHANT m ON m.id = rr.merchant_id\n" +
            "                                                WHERE user_id = :user_id AND status_id IN (4);"; //TODO configure statuses
    @Autowired
    @Qualifier(value = "slaveTemplate")
    private NamedParameterJdbcTemplate slaveTemplate;

    @Override
    public List<RefillPendingRequestDto> getPendingRefillRequests(long userId) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("user_id", userId);

        return slaveTemplate.query(GET_PENDING_REQUESTS, sqlParameterSource, RefillPendingRequestDto.builder().build());
    }

}
