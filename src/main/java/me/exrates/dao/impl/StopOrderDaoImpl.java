package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.StopOrderDao;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.main.CurrencyPair;
import me.exrates.model.main.StopOrder;
import me.exrates.model.onlineTableDto.OrderWideListDto;
import me.exrates.util.BigDecimalProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Log4j2
public class StopOrderDaoImpl implements StopOrderDao {

    private RowMapper<StopOrder> getStopOrdersRowMapper() {
        return (resultSet, i) -> {
            final StopOrder stopOrder = new StopOrder();
            stopOrder.setId(resultSet.getInt("id"));
            stopOrder.setUserId(resultSet.getInt("user_id"));
            stopOrder.setCurrencyPairId(resultSet.getInt("currency_pair_id"));
            stopOrder.setOperationType(OperationType.convert(resultSet.getInt("operation_type_id")));
            stopOrder.setStatus(OrderStatus.convert(resultSet.getInt("status_id")));
            stopOrder.setStop(resultSet.getBigDecimal("stop_rate"));
            stopOrder.setLimit(resultSet.getBigDecimal("limit_rate"));
            stopOrder.setAmountBase(resultSet.getBigDecimal("amount_base"));
            stopOrder.setAmountConvert(resultSet.getBigDecimal("amount_convert"));
            stopOrder.setComissionId(resultSet.getInt("commission_id"));
            stopOrder.setCommissionFixedAmount(resultSet.getBigDecimal("commission_fixed_amount"));
            stopOrder.setDateCreation(resultSet.getTimestamp("date_creation").toLocalDateTime());
            Timestamp modTimestamp = resultSet.getTimestamp("date_modification");
            stopOrder.setModificationDate(modTimestamp == null ? null : modTimestamp.toLocalDateTime());
            if (stopOrder.getStatus().equals(OrderStatus.CLOSED)) {
                stopOrder.setChildOrderId(resultSet.getInt("child_order_id"));
            }
            return stopOrder;
        };
    }


    @Autowired
//    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public boolean setStatus(int orderId, OrderStatus status) {
        String sql = "UPDATE STOP_ORDERS SET status_id=:status_id WHERE id = :id";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("status_id", String.valueOf(status.getStatus()));
        namedParameters.put("id", String.valueOf(orderId));
        int result = namedParameterJdbcTemplate.update(sql, namedParameters);
        return result > 0;
    }

    @Override
    public Integer create(StopOrder order) {
        String sql = "INSERT INTO STOP_ORDERS" +
                "  (user_id, currency_pair_id, operation_type_id, stop_rate,  limit_rate, amount_base, amount_convert, commission_id, commission_fixed_amount, status_id)" +
                "  VALUES " +
                "  (:user_id, :currency_pair_id, :operation_type_id, :stop_rate, :limit_rate, :amount_base, :amount_convert, :commission_id, :commission_fixed_amount, :status_id)";
        log.debug(sql);
        log.debug(order);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_id", order.getUserId())
                .addValue("currency_pair_id", order.getCurrencyPairId())
                .addValue("operation_type_id", order.getOperationType().getType())
                .addValue("stop_rate", order.getStop())
                .addValue("limit_rate", order.getLimit())
                .addValue("amount_base", order.getAmountBase())
                .addValue("amount_convert", order.getAmountConvert())
                .addValue("commission_id", order.getComissionId())
                .addValue("commission_fixed_amount", order.getCommissionFixedAmount())
                .addValue("status_id", OrderStatus.INPROCESS.getStatus());
        int result = namedParameterJdbcTemplate.update(sql, parameters, keyHolder);
        int id = (int) keyHolder.getKey().longValue();
        if (result <= 0) {
            id = 0;
        }
        return id;
    }

    @Override
    public boolean setStatusAndChildOrderId(int orderId, Integer childOrderId, OrderStatus status) {
        String sql = "UPDATE STOP_ORDERS SET status_id=:status_id, child_order_id=:child_order_id " +
                "WHERE id = :id";
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("status_id", status.getStatus());
        namedParameters.put("id", orderId);
        namedParameters.put("child_order_id", childOrderId);
        int result = namedParameterJdbcTemplate.update(sql, namedParameters);
        return result > 0;
    }

    @Override
    public OrderCreateDto getOrderById(Integer orderId, boolean forUpdate) {
        String sql = "SELECT SO.id as order_id, SO.user_id, SO.status_id, SO.operation_type_id,  " +
                "  SO.limit_rate, SO.stop_rate, SO.amount_base, SO.amount_convert, SO.commission_fixed_amount, " +
                "  CURRENCY_PAIR.id AS currency_pair_id, CURRENCY_PAIR.name AS currency_pair_name  " +
                "  FROM STOP_ORDERS AS SO " +
                "  LEFT JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = SO.currency_pair_id) " +
                "  WHERE SO.id = :order_id";
        if (forUpdate) {
            sql = sql.concat(" FOR UPDATE");
        }
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("order_id", orderId);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new RowMapper<OrderCreateDto>() {
                @Override
                public OrderCreateDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    OrderCreateDto orderCreateDto = new OrderCreateDto();
                    orderCreateDto.setOrderId(rs.getInt("order_id"));
                    orderCreateDto.setUserId(rs.getInt("user_id"));
                    orderCreateDto.setOperationType(OperationType.convert(rs.getInt("operation_type_id")));
                    orderCreateDto.setStatus(OrderStatus.convert(rs.getInt("status_id")));
                    orderCreateDto.setExchangeRate(rs.getBigDecimal("limit_rate"));
                    orderCreateDto.setStop(rs.getBigDecimal("stop_rate"));
                    CurrencyPair currencyPair = new CurrencyPair();
                    currencyPair.setId(rs.getInt("currency_pair_id"));
                    currencyPair.setName(rs.getString("currency_pair_name"));
                    orderCreateDto.setCurrencyPair(currencyPair);
                    orderCreateDto.setAmount(rs.getBigDecimal("amount_base"));
                    orderCreateDto.setTotal(rs.getBigDecimal("amount_convert"));
                    orderCreateDto.setComission(rs.getBigDecimal("commission_fixed_amount"));
                    if (orderCreateDto.getOperationType() == OperationType.SELL) {
                        orderCreateDto.setTotalWithComission(BigDecimalProcessing.doAction(orderCreateDto.getTotal(), orderCreateDto.getComission(), ActionType.SUBTRACT));
                    } else {
                        orderCreateDto.setTotalWithComission(BigDecimalProcessing.doAction(orderCreateDto.getTotal(), orderCreateDto.getComission(), ActionType.ADD));
                    }
                    return orderCreateDto;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<OrderWideListDto> getMyOrdersWithState(String email, CurrencyPair currencyPair, OrderStatus status, OperationType operationType, String scope, Integer offset, Integer limit, Locale locale) {
        return getMyOrdersWithState(email, currencyPair, Collections.singletonList(status), operationType, scope, offset, limit, locale);
    }

    @Override
    public List<StopOrder> getOrdersBypairId(List<Integer> pairIds, OrderStatus status) {
        String sql = "SELECT * FROM STOP_ORDERS AS SO " +
                "INNER JOIN CURRENCY_PAIR AS CP ON CP.ID = SO.currency_pair_id " +
                "WHERE SO.currency_pair_id IN (:pairsId) AND SO.status_id = :statusId";
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("pairsId", pairIds);
        namedParameters.put("statusId", status.getStatus());
        try {
            return namedParameterJdbcTemplate.query(sql, namedParameters, getStopOrdersRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<OrderWideListDto> getMyOrdersWithState(String email, CurrencyPair currencyPair, List<OrderStatus> statuses,
                                                       OperationType operationType,
                                                       String scope, Integer offset, Integer limit, Locale locale) {
        String userFilterClause;
        String userJoinClause;
        if (scope == null || scope.isEmpty()) {
            userFilterClause = " AND CREATOR.email = :email ";
            userJoinClause = "  JOIN USER AS CREATOR ON CREATOR.id=STOP_ORDERS.user_id ";
        } else {
            switch (scope) {
                default:
                    userFilterClause = " AND CREATOR.email = :email ";
                    userJoinClause = "  JOIN USER AS CREATOR ON CREATOR.id=STOP_ORDERS.user_id ";
                    break;
            }
        }

        List<Integer> statusIds = statuses.stream().map(OrderStatus::getStatus).collect(Collectors.toList());
        String orderClause = "  ORDER BY  date_creation DESC";
        String sql = "SELECT STOP_ORDERS.*, CURRENCY_PAIR.name AS currency_pair_name" +
                "  FROM STOP_ORDERS " +
                userJoinClause +
                "  JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = STOP_ORDERS.currency_pair_id) " +
                "  WHERE (status_id IN (:status_ids))" +
                userFilterClause +
                (currencyPair == null ? "" : " AND STOP_ORDERS.currency_pair_id=" + currencyPair.getId()) +
                orderClause +
                (limit == -1 ? "" : "  LIMIT " + limit + " OFFSET " + offset);
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("email", email);
        namedParameters.put("status_ids", statusIds);
        if (operationType != null) {
            namedParameters.put("operation_type_id", operationType.getType());
            sql = sql.concat(" AND (operation_type_id = :operation_type_id) ");
        }
        return namedParameterJdbcTemplate.query(sql, namedParameters, new RowMapper<OrderWideListDto>() {
            @Override
            public OrderWideListDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                OrderWideListDto orderWideListDto = new OrderWideListDto();
                orderWideListDto.setId(rs.getInt("id"));
                orderWideListDto.setUserId(rs.getInt("user_id"));
                orderWideListDto.setOperationTypeEnum(OperationType.convert(rs.getInt("operation_type_id")));
                orderWideListDto.setOperationType(orderWideListDto.getOperationTypeEnum().name());
                orderWideListDto.setStopRate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("stop_rate"), locale, 2));
                orderWideListDto.setExExchangeRate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("limit_rate"), locale, 2));
                orderWideListDto.setAmountBase(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_base"), locale, 2));
                orderWideListDto.setAmountConvert(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_convert"), locale, 2));
                orderWideListDto.setComissionId(rs.getInt("commission_id"));
                orderWideListDto.setCommissionFixedAmount(BigDecimalProcessing.formatLocale(rs.getBigDecimal("commission_fixed_amount"), locale, 2));
                BigDecimal amountWithCommission = rs.getBigDecimal("amount_convert");
                if (orderWideListDto.getOperationTypeEnum() == OperationType.SELL) {
                    amountWithCommission = BigDecimalProcessing.doAction(amountWithCommission, rs.getBigDecimal("commission_fixed_amount"), ActionType.SUBTRACT);
                } else if (orderWideListDto.getOperationTypeEnum() == OperationType.BUY) {
                    amountWithCommission = BigDecimalProcessing.doAction(amountWithCommission, rs.getBigDecimal("commission_fixed_amount"), ActionType.ADD);
                }
                orderWideListDto.setAmountWithCommission(BigDecimalProcessing.formatLocale(amountWithCommission, locale, 2));
                orderWideListDto.setDateCreation(rs.getTimestamp("date_creation") == null ? null : rs.getTimestamp("date_creation").toLocalDateTime());
                orderWideListDto.setStatus(OrderStatus.convert(rs.getInt("status_id")));
                orderWideListDto.setDateStatusModification(rs.getTimestamp("date_modification") == null ? null : rs.getTimestamp("date_modification").toLocalDateTime());
                orderWideListDto.setCurrencyPairId(rs.getInt("currency_pair_id"));
                orderWideListDto.setCurrencyPairName(rs.getString("currency_pair_name"));
                orderWideListDto.setOrderBaseType(OrderBaseType.STOP_LIMIT);
                return orderWideListDto;
            }
        });
    }
}
