package me.exrates.dao.impl;

import me.exrates.dao.CommissionDao;
import me.exrates.dao.OrderDao;
import me.exrates.dao.WalletDao;
import me.exrates.dao.rowmapper.OrderRowMapper;
import me.exrates.exception.OrderDaoException;
import me.exrates.model.StatisticForMarket;
import me.exrates.model.dto.*;
import me.exrates.model.enums.*;
import me.exrates.model.main.BackDealInterval;
import me.exrates.model.main.Currency;
import me.exrates.model.main.CurrencyPair;
import me.exrates.model.main.ExOrder;
import me.exrates.model.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.onlineTableDto.OrderAcceptedHistoryDto;
import me.exrates.model.onlineTableDto.OrderListDto;
import me.exrates.model.onlineTableDto.OrderWideListDto;
import me.exrates.util.BigDecimalProcessing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class OrderDaoImpl implements OrderDao {


    private static final Logger LOGGER = LogManager.getLogger(OrderDaoImpl.class);

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    @Qualifier("slaveTemplate")
    private NamedParameterJdbcTemplate slaveJdbcTemplate;


    private static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    @Autowired
    CommissionDao commissionDao;

    @Autowired
    WalletDao walletDao;

    public int createOrder(ExOrder exOrder) {
        String sql = "INSERT INTO EXORDERS" +
                "  (user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_id, commission_fixed_amount, status_id, order_source_id, base_type)" +
                "  VALUES " +
                "  (:user_id, :currency_pair_id, :operation_type_id, :exrate, :amount_base, :amount_convert, :commission_id, :commission_fixed_amount, :status_id, :order_source_id, :base_type)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_id", exOrder.getUserId())
                .addValue("currency_pair_id", exOrder.getCurrencyPairId())
                .addValue("operation_type_id", exOrder.getOperationType().getType())
                .addValue("exrate", exOrder.getExRate())
                .addValue("amount_base", exOrder.getAmountBase())
                .addValue("amount_convert", exOrder.getAmountConvert())
                .addValue("commission_id", exOrder.getComissionId())
                .addValue("commission_fixed_amount", exOrder.getCommissionFixedAmount())
                .addValue("status_id", OrderStatus.INPROCESS.getStatus())
                .addValue("order_source_id", exOrder.getSourceId())
                .addValue("base_type", exOrder.getOrderBaseType().name());
        int result = namedParameterJdbcTemplate.update(sql, parameters, keyHolder);
        int id = (int) keyHolder.getKey().longValue();
        if (result <= 0) {
            id = 0;
        }
        return id;
    }

    @Override
    public Optional<BigDecimal> getLowestOpenOrderPriceByCurrencyPairAndOperationType(int currencyPairId, int operationTypeId) {
        String sql = "SELECT exrate FROM EXORDERS WHERE status_id = 2 AND currency_pair_id = :currency_pair_id AND operation_type_id = :operation_type_id " +
                "ORDER BY exrate ASC  LIMIT 1";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", currencyPairId);
        namedParameters.put("operation_type_id", operationTypeId);
        try {
            return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, namedParameters, BigDecimal.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public ExOrder getOrderById(int orderId) {
        String sql = "SELECT * FROM EXORDERS WHERE id = :id";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("id", String.valueOf(orderId));
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new OrderRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean setStatus(int orderId, OrderStatus status) {
        String sql = "UPDATE EXORDERS SET status_id=:status_id WHERE id = :id";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("status_id", String.valueOf(status.getStatus()));
        namedParameters.put("id", String.valueOf(orderId));
        int result = namedParameterJdbcTemplate.update(sql, namedParameters);
        return result > 0;
    }

    @Override
    public boolean updateOrder(ExOrder exOrder) {
        String sql = "update EXORDERS set user_acceptor_id=:user_acceptor_id, status_id=:status_id, " +
                " date_acception=NOW()  " +
                " where id = :id";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("user_acceptor_id", String.valueOf(exOrder.getUserAcceptorId()));
        namedParameters.put("status_id", String.valueOf(exOrder.getStatus().getStatus()));
//        namedParameters.put("date_acception", String.valueOf(exOrder.getDateAcception()));
        namedParameters.put("id", String.valueOf(exOrder.getId()));
        int result = namedParameterJdbcTemplate.update(sql, namedParameters);
        return result > 0;
    }

    @Override
    public boolean updateOrder(int orderId, ExOrder exOrder) {

        String sql = "UPDATE EXORDERS SET" +
                " user_id = :user_id, currency_pair_id = :currency_pair_id, operation_type_id = :operation_type_id," +
                " exrate = :exrate, amount_base = :amount_base, amount_convert = :amount_convert, commission_id = :commission_id," +
                " commission_fixed_amount = :commission_fixed_amount, status_id = :status_id, order_source_id = :order_source_id," +
                " base_type = :base_type" +
                "  WHERE id = :id ";
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_id", exOrder.getUserId())
                .addValue("currency_pair_id", exOrder.getCurrencyPairId())
                .addValue("operation_type_id", exOrder.getOperationType().getType())
                .addValue("exrate", exOrder.getExRate())
                .addValue("amount_base", exOrder.getAmountBase())
                .addValue("amount_convert", exOrder.getAmountConvert())
                .addValue("commission_id", exOrder.getComissionId())
                .addValue("commission_fixed_amount", exOrder.getCommissionFixedAmount())
                .addValue("status_id", exOrder.getStatus().getStatus())
                .addValue("order_source_id", exOrder.getSourceId())
                .addValue("base_type", exOrder.getOrderBaseType().name())
                .addValue("id", orderId);
        return namedParameterJdbcTemplate.update(sql, parameters) > 0;
    }

    @Override
    public List<OrderListDto> getOrdersBuyForCurrencyPair(CurrencyPair currencyPair, UserRole filterRole) {
        String sql = "SELECT EXORDERS.id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_fixed_amount" +
                "  FROM EXORDERS " +
                (filterRole == null ? "" : " JOIN USER ON (USER.id=EXORDERS.user_id)  AND USER.roleid = :user_role_id ") +
                "  WHERE status_id = 2 and operation_type_id= 4 and currency_pair_id=:currency_pair_id" +
                "  ORDER BY exrate DESC";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", currencyPair.getId());
        if (filterRole != null) {
            namedParameters.put("user_role_id", filterRole.getRole());
        }
        return slaveJdbcTemplate.query(sql, namedParameters, (rs, row) -> {
            OrderListDto order = new OrderListDto();
            order.setId(rs.getInt("id"));
            order.setUserId(rs.getInt("user_id"));
            order.setOrderType(OperationType.convert(rs.getInt("operation_type_id")));
            order.setExrate(rs.getString("exrate"));
            order.setAmountBase(rs.getString("amount_base"));
            order.setAmountConvert(rs.getString("amount_convert"));
            return order;
        });
    }

    @Override
    public List<OrderListDto> getOrdersSellForCurrencyPair(CurrencyPair currencyPair, UserRole filterRole) {
        String sql = "SELECT EXORDERS.id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_fixed_amount" +
                "  FROM EXORDERS " +
                (filterRole == null ? "" : " JOIN USER ON (USER.id=EXORDERS.user_id)  AND USER.roleid = :user_role_id ") +
                "  WHERE status_id = 2 and operation_type_id= 3 and currency_pair_id=:currency_pair_id" +
                "  ORDER BY exrate ASC";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", currencyPair.getId());
        if (filterRole != null) {
            namedParameters.put("user_role_id", filterRole.getRole());
        }
        return slaveJdbcTemplate.query(sql, namedParameters, (rs, row) -> {
            OrderListDto order = new OrderListDto();
            order.setId(rs.getInt("id"));
            order.setUserId(rs.getInt("user_id"));
            order.setOrderType(OperationType.convert(rs.getInt("operation_type_id")));
            order.setExrate(rs.getString("exrate"));
            order.setAmountBase(rs.getString("amount_base"));
            order.setAmountConvert(rs.getString("amount_convert"));
            return order;
        });
    }

    @Override
    public List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, BackDealInterval backDealInterval) {
        return getCandleChartData(currencyPair, backDealInterval, "NOW()");
    }

    public List<ExOrderStatisticsShortByPairsDto> getOrderStatisticByPairs() {
        long before = System.currentTimeMillis();
        try {
            String sql =
                    "SELECT RESULT.currency_pair_name, RESULT.currency_pair_id, RESULT.type, RESULT.last_exrate, RESULT.pred_last_exrate, RESULT.pair_order " +
                            "FROM " +
                            "((SELECT  " +
                            "   CURRENCY_PAIR.name AS currency_pair_name, CURRENCY_PAIR.id AS currency_pair_id, CURRENCY_PAIR.type AS type, " +
                            "   (SELECT LASTORDER.exrate " +
                            "       FROM EXORDERS LASTORDER  " +
                            "       WHERE  " +
                            "       (LASTORDER.currency_pair_id =AGRIGATE.currency_pair_id)  AND  " +
                            "       (LASTORDER.status_id =AGRIGATE.status_id) " +
                            "       ORDER BY LASTORDER.date_acception DESC, LASTORDER.id DESC " +
                            "       LIMIT 1) AS last_exrate, " +
                            "   (SELECT PRED_LASTORDER.exrate " +
                            "       FROM EXORDERS PRED_LASTORDER  " +
                            "       WHERE  " +
                            "       (PRED_LASTORDER.currency_pair_id =AGRIGATE.currency_pair_id)  AND  " +
                            "       (PRED_LASTORDER.status_id =AGRIGATE.status_id) " +
                            "       ORDER BY PRED_LASTORDER.date_acception DESC, PRED_LASTORDER.id DESC " +
                            "       LIMIT 1,1) AS pred_last_exrate, CURRENCY_PAIR.pair_order  " +
                            " FROM ( " +
                            "   SELECT DISTINCT" +
                            "   EXORDERS.status_id AS status_id,  " +
                            "   EXORDERS.currency_pair_id AS currency_pair_id " +
                            "   FROM EXORDERS          " +
                            "   WHERE EXORDERS.status_id = :status_id         " +
                            "   ) " +
                            " AGRIGATE " +
                            " JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = AGRIGATE.currency_pair_id) AND (CURRENCY_PAIR.hidden != 1) " +
                            " ORDER BY -CURRENCY_PAIR.pair_order DESC)" +
                            " UNION ALL (" +
                            "   SELECT CP.name AS currency_pair_name, CP.id AS currency_pair_id, CP.type AS type, 0 AS last_exrate, 0 AS pred_last_exrate, CP.pair_order " +
                            "      FROM CURRENCY_PAIR CP " +
                            "      WHERE CP.id NOT IN(SELECT DISTINCT EXORDERS.currency_pair_id AS currency_pair_id FROM EXORDERS WHERE EXORDERS.status_id = :status_id) AND CP.hidden = 0 " +
                            ")) RESULT ";
            Map<String, String> namedParameters = new HashMap<>();
            namedParameters.put("status_id", String.valueOf(3));
            return slaveJdbcTemplate.query(sql, namedParameters, exchangeRatesRowMapper);
        } catch (Exception e) {
            long after = System.currentTimeMillis();
            LOGGER.error("error... ms: " + (after - before) + " : " + e);
            throw new OrderDaoException(e);
        } finally {
            long after = System.currentTimeMillis();
            LOGGER.debug("query completed ... ms: " + (after - before));
        }
    }

    RowMapper<ExOrderStatisticsShortByPairsDto> exchangeRatesRowMapper = (rs, rowNum) -> {
        ExOrderStatisticsShortByPairsDto exOrderStatisticsDto = new ExOrderStatisticsShortByPairsDto();
        exOrderStatisticsDto.setCurrencyPairName(rs.getString("currency_pair_name"));
        exOrderStatisticsDto.setCurrencyPairId(rs.getInt("currency_pair_id"));
        exOrderStatisticsDto.setLastOrderRate(rs.getString("last_exrate"));
        exOrderStatisticsDto.setPredLastOrderRate(rs.getString("pred_last_exrate"));
        exOrderStatisticsDto.setType(CurrencyPairType.valueOf(rs.getString("type")));
        return exOrderStatisticsDto;
    };

    @Override
    public List<CoinmarketApiDto> getCoinmarketData(String currencyPairName) {
        String s = "{call GET_COINMARKETCAP_STATISTICS('" + currencyPairName + "')}";
        List<CoinmarketApiDto> result = namedParameterJdbcTemplate.execute(s, new PreparedStatementCallback<List<CoinmarketApiDto>>() {
            @Override
            public List<CoinmarketApiDto> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                ResultSet rs = ps.executeQuery();
                List<CoinmarketApiDto> list = new ArrayList();
                while (rs.next()) {
                    CoinmarketApiDto coinmarketApiDto = new CoinmarketApiDto();
                    coinmarketApiDto.setCurrencyPairId(rs.getInt("currency_pair_id"));
                    coinmarketApiDto.setCurrency_pair_name(rs.getString("currency_pair_name"));
                    coinmarketApiDto.setFirst(rs.getBigDecimal("first"));
                    coinmarketApiDto.setLast(rs.getBigDecimal("last"));
                    coinmarketApiDto.setLowestAsk(rs.getBigDecimal("lowestAsk"));
                    coinmarketApiDto.setHighestBid(rs.getBigDecimal("highestBid"));
                    coinmarketApiDto.setPercentChange(BigDecimalProcessing.doAction(coinmarketApiDto.getFirst(), coinmarketApiDto.getLast(), ActionType.PERCENT_GROWTH));
                    coinmarketApiDto.setBaseVolume(rs.getBigDecimal("baseVolume"));
                    coinmarketApiDto.setQuoteVolume(rs.getBigDecimal("quoteVolume"));
                    coinmarketApiDto.setIsFrozen(rs.getInt("isFrozen"));
                    coinmarketApiDto.setHigh24hr(rs.getBigDecimal("high24hr"));
                    coinmarketApiDto.setLow24hr(rs.getBigDecimal("low24hr"));
                    list.add(coinmarketApiDto);
                }
                rs.close();
                return list;
            }
        });
        return result;
    }

    @Override
    public OrderInfoDto getOrderInfo(int orderId, Locale locale) {
        String sql =
                " SELECT  " +
                        "     EXORDERS.id, EXORDERS.date_creation, EXORDERS.date_acception, EXORDERS.base_type, " +
                        "     ORDER_STATUS.name AS order_status_name,  " +
                        "     CURRENCY_PAIR.name as currency_pair_name,  " +
                        "     UPPER(ORDER_OPERATION.name) AS order_type_name,  " +
                        "     EXORDERS.exrate, EXORDERS.amount_base, EXORDERS.amount_convert, " +
                        "     ORDER_CURRENCY_BASE.name as currency_base_name, ORDER_CURRENCY_CONVERT.name as currency_convert_name, " +
                        "     CREATOR.email AS order_creator_email, " +
                        "     ACCEPTOR.email AS order_acceptor_email, " +
                        "     COUNT(TRANSACTION.id) AS transaction_count,  " +
                        "     SUM(TRANSACTION.commission_amount) AS company_commission," +
                        "     EXORDERS.order_source_id AS source_id  " +
                        " FROM EXORDERS " +
                        "      JOIN ORDER_STATUS ON (ORDER_STATUS.id = EXORDERS.status_id) " +
                        "      JOIN OPERATION_TYPE AS ORDER_OPERATION ON (ORDER_OPERATION.id = EXORDERS.operation_type_id) " +
                        "      JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = EXORDERS.currency_pair_id) " +
                        "      JOIN CURRENCY ORDER_CURRENCY_BASE ON (ORDER_CURRENCY_BASE.id = CURRENCY_PAIR.currency1_id)   " +
                        "      JOIN CURRENCY ORDER_CURRENCY_CONVERT ON (ORDER_CURRENCY_CONVERT.id = CURRENCY_PAIR.currency2_id)  " +
                        "      JOIN WALLET ORDER_CREATOR_RESERVED_WALLET ON  " +
                        "              (ORDER_CREATOR_RESERVED_WALLET.user_id=EXORDERS.user_id) AND  " +
                        "              ( " +
                        "                  (upper(ORDER_OPERATION.name)='BUY' AND ORDER_CREATOR_RESERVED_WALLET.currency_id = CURRENCY_PAIR.currency2_id)  " +
                        "                  OR  " +
                        "                  (upper(ORDER_OPERATION.name)='SELL' AND ORDER_CREATOR_RESERVED_WALLET.currency_id = CURRENCY_PAIR.currency1_id) " +
                        "              ) " +
                        "      JOIN USER CREATOR ON (CREATOR.id = EXORDERS.user_id) " +
                        "      LEFT JOIN USER ACCEPTOR ON (ACCEPTOR.id = EXORDERS.user_acceptor_id) " +
                        "      LEFT JOIN TRANSACTION ON (TRANSACTION.source_type='ORDER') AND (TRANSACTION.source_id = EXORDERS.id) " +
                        "      LEFT JOIN OPERATION_TYPE TRANSACTION_OPERATION ON (TRANSACTION_OPERATION.id = TRANSACTION.operation_type_id) " +
                        "      LEFT JOIN WALLET USER_WALLET ON (USER_WALLET.id = TRANSACTION.user_wallet_id) " +
                        "      LEFT JOIN COMPANY_WALLET ON (COMPANY_WALLET.currency_id = TRANSACTION.company_wallet_id) and (TRANSACTION.commission_amount <> 0) " +
                        "      LEFT JOIN USER ON (USER.id = USER_WALLET.user_id) " +
                        " WHERE EXORDERS.id=:order_id" +
                        " GROUP BY " +
                        "     EXORDERS.id, EXORDERS.date_creation, EXORDERS.date_acception,  " +
                        "     order_status_name,  " +
                        "     currency_pair_name,  " +
                        "     order_type_name,  " +
                        "     EXORDERS.exrate, EXORDERS.amount_base, EXORDERS.amount_convert, " +
                        "     currency_base_name, currency_convert_name, " +
                        "     order_creator_email, " +
                        "     order_acceptor_email ";
        Map<String, String> mapParameters = new HashMap<>();
        mapParameters.put("order_id", String.valueOf(orderId));
        try {
            return slaveJdbcTemplate.queryForObject(sql, mapParameters, new RowMapper<OrderInfoDto>() {
                @Override
                public OrderInfoDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    OrderInfoDto orderInfoDto = new OrderInfoDto();
                    OrderBaseType orderBaseType = OrderBaseType.valueOf(rs.getString("base_type"));
                    orderInfoDto.setId(rs.getInt("id"));
                    orderInfoDto.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
                    orderInfoDto.setDateAcception(rs.getTimestamp("date_acception") == null ? null : rs.getTimestamp("date_acception").toLocalDateTime());
                    orderInfoDto.setCurrencyPairName(rs.getString("currency_pair_name"));
                    orderInfoDto.setOrderTypeName(rs.getString("order_type_name").concat(" ").concat(orderBaseType.name()));
                    orderInfoDto.setOrderStatusName(rs.getString("order_status_name"));
                    orderInfoDto.setExrate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("exrate"), locale, 2));
                    orderInfoDto.setAmountBase(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_base"), locale, 2));
                    orderInfoDto.setAmountConvert(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_convert"), locale, 2));
                    orderInfoDto.setCurrencyBaseName(rs.getString("currency_base_name"));
                    orderInfoDto.setCurrencyConvertName(rs.getString("currency_convert_name"));
                    orderInfoDto.setOrderCreatorEmail(rs.getString("order_creator_email"));
                    orderInfoDto.setOrderAcceptorEmail(rs.getString("order_acceptor_email"));
                    orderInfoDto.setTransactionCount(BigDecimalProcessing.formatLocale(rs.getBigDecimal("transaction_count"), locale, 2));
                    orderInfoDto.setCompanyCommission(BigDecimalProcessing.formatLocale(rs.getBigDecimal("company_commission"), locale, 2));
                    orderInfoDto.setSource((Integer) rs.getObject("source_id"));
                    orderInfoDto.setChildren(getOrderChildren(orderId));
                    return orderInfoDto;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private List<Integer> getOrderChildren(int id) {
        String sql = "SELECT id FROM EXORDERS WHERE order_source_id = :id";
        return slaveJdbcTemplate.queryForList(sql, Collections.singletonMap("id", id), Integer.class);
    }


    public OrderCreateDto getMyOrderById(int orderId) {
        String sql = "SELECT EXORDERS.id as order_id, EXORDERS.user_id, EXORDERS.status_id, EXORDERS.operation_type_id,  " +
                "  EXORDERS.exrate, EXORDERS.amount_base, EXORDERS.amount_convert, EXORDERS.commission_fixed_amount, " +
                "  CURRENCY_PAIR.id AS currency_pair_id, CURRENCY_PAIR.name AS currency_pair_name, EXORDERS.base_type  " +
                "  FROM EXORDERS " +
                "  LEFT JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = EXORDERS.currency_pair_id) " +
                "  WHERE (EXORDERS.id = :order_id)";
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
                    orderCreateDto.setExchangeRate(rs.getBigDecimal("exrate"));
                    CurrencyPair currencyPair = new CurrencyPair();
                    currencyPair.setId(rs.getInt("currency_pair_id"));
                    currencyPair.setName(rs.getString("currency_pair_name"));
                    orderCreateDto.setCurrencyPair(currencyPair);
                    orderCreateDto.setAmount(rs.getBigDecimal("amount_base"));
                    orderCreateDto.setTotal(rs.getBigDecimal("amount_convert"));
                    orderCreateDto.setComission(rs.getBigDecimal("commission_fixed_amount"));
                    orderCreateDto.setOrderBaseType(OrderBaseType.valueOf(rs.getString("base_type")));
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
    public boolean lockOrdersListForAcception(List<Integer> ordersList) {
        //TODO Why cycle?? not WHERE id IN (...) ?

        for (Integer orderId : ordersList) {
            String sql = "SELECT id " +
                    "  FROM EXORDERS " +
                    "  WHERE id = :order_id " +
                    "  FOR UPDATE ";
            Map<String, Object> namedParameters = new HashMap<>();
            namedParameters.put("order_id", orderId);
            try {
                namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
            } catch (EmptyResultDataAccessException e) {
                return false;
            }
        }
        return true;
    }


    public List<ExOrder> selectTopOrders(Integer currencyPairId, BigDecimal exrate,
                                         OperationType orderType, boolean sameRoleOnly, Integer userAcceptorRoleId, OrderBaseType orderBaseType) {
        String sortDirection = "";
        String exrateClause = "";
        if (orderType == OperationType.BUY) {
            sortDirection = "DESC";
            exrateClause = "AND EO.exrate >= :exrate ";
        } else if (orderType == OperationType.SELL) {
            sortDirection = "ASC";
            exrateClause = "AND EO.exrate <= :exrate ";
        }
        String roleJoinClause = sameRoleOnly ? " JOIN USER U ON EO.user_id = U.id AND U.roleid = :acceptor_role_id " :
                "JOIN USER U ON EO.user_id = U.id AND U.roleid IN (SELECT user_role_id FROM USER_ROLE_SETTINGS " +
                        "WHERE user_role_id = :acceptor_role_id OR order_acception_same_role_only = 0)";
        String sqlSetVar = "SET @cumsum := 0";

        /*needs to return several orders with best exrate if their total sum is less than amount in param,
         * or at least one order if base amount is greater than param amount*/
        String sql = "SELECT EO.id, EO.user_id, EO.currency_pair_id, EO.operation_type_id, EO.exrate, EO.amount_base, EO.amount_convert, " +
                "EO.commission_id, EO.commission_fixed_amount, EO.date_creation, EO.status_id, EO.base_type " +
                "FROM EXORDERS EO " + roleJoinClause +
                "WHERE EO.status_id = 2 AND EO.currency_pair_id = :currency_pair_id AND EO.base_type =:order_base_type " +
                "AND EO.operation_type_id = :operation_type_id " + exrateClause +
                " ORDER BY EO.exrate " + sortDirection + ", EO.amount_base ASC ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("currency_pair_id", currencyPairId);
            put("exrate", exrate);
            put("operation_type_id", orderType.getType());
            put("acceptor_role_id", userAcceptorRoleId);
            put("order_base_type", orderBaseType.name());
        }};
        namedParameterJdbcTemplate.execute(sqlSetVar, PreparedStatement::execute);

        return namedParameterJdbcTemplate.query(sql, params, (rs, row) -> {
            ExOrder exOrder = new ExOrder();
            exOrder.setId(rs.getInt("id"));
            exOrder.setUserId(rs.getInt("user_id"));
            exOrder.setCurrencyPairId(rs.getInt("currency_pair_id"));
            exOrder.setOperationType(OperationType.convert(rs.getInt("operation_type_id")));
            exOrder.setExRate(rs.getBigDecimal("exrate"));
            exOrder.setAmountBase(rs.getBigDecimal("amount_base"));
            exOrder.setAmountConvert(rs.getBigDecimal("amount_convert"));
            exOrder.setComissionId(rs.getInt("commission_id"));
            exOrder.setCommissionFixedAmount(rs.getBigDecimal("commission_fixed_amount"));
            exOrder.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
            exOrder.setStatus(OrderStatus.convert(rs.getInt("status_id")));
            exOrder.setOrderBaseType(OrderBaseType.valueOf(rs.getString("base_type")));
            return exOrder;
        });
    }

    @Override
    public WalletsAndCommissionsForOrderCreationDto getWalletAndCommission(String email, Currency currency,
                                                                           OperationType operationType, UserRole userRole) {
        String sql = "SELECT USER.id AS user_id, WALLET.id AS wallet_id, WALLET.active_balance, COMM.id AS commission_id, COMM.value AS commission_value" +
                "  FROM USER " +
                "    LEFT JOIN WALLET ON (WALLET.user_id=USER.id) AND (WALLET.currency_id = :currency_id) " +
                "    LEFT JOIN ((SELECT COMMISSION.id, COMMISSION.value " +
                "           FROM COMMISSION " +
                "           WHERE COMMISSION.operation_type=:operation_type_id AND COMMISSION.user_role = :user_role ORDER BY COMMISSION.date " +
                "           DESC LIMIT 1) AS COMM) ON (1=1) " +
                "  WHERE USER.email = :email";
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("email", email);
        namedParameters.put("operation_type_id", operationType.getType());
        namedParameters.put("currency_id", currency.getId());
        namedParameters.put("user_role", userRole.getRole());
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new RowMapper<WalletsAndCommissionsForOrderCreationDto>() {
                @Override
                public WalletsAndCommissionsForOrderCreationDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    WalletsAndCommissionsForOrderCreationDto walletsAndCommissionsForOrderCreationDto = new WalletsAndCommissionsForOrderCreationDto();
                    walletsAndCommissionsForOrderCreationDto.setUserId(rs.getInt("user_id"));
                    walletsAndCommissionsForOrderCreationDto.setSpendWalletId(rs.getInt("wallet_id"));
                    walletsAndCommissionsForOrderCreationDto.setSpendWalletActiveBalance(rs.getBigDecimal("active_balance"));
                    walletsAndCommissionsForOrderCreationDto.setCommissionId(rs.getInt("commission_id"));
                    walletsAndCommissionsForOrderCreationDto.setCommissionValue(rs.getBigDecimal("commission_value"));
                    return walletsAndCommissionsForOrderCreationDto;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<OrderAcceptedHistoryDto> getOrderAcceptedForPeriod(String email, BackDealInterval backDealInterval, Integer limit, CurrencyPair currencyPair) {
        String sql = "SELECT EXORDERS.id, EXORDERS.date_acception, EXORDERS.exrate, EXORDERS.amount_base, EXORDERS.operation_type_id " +
                "  FROM EXORDERS " +
                (email == null || email.isEmpty() ? "" : " JOIN USER ON ((USER.id = EXORDERS.user_id) OR (USER.id = EXORDERS.user_acceptor_id)) AND USER.email='" + email + "'") +
                "  WHERE EXORDERS.status_id = :status " +
                "  AND EXORDERS.date_acception >= now() - INTERVAL " + backDealInterval.getInterval() +
                "  AND EXORDERS.currency_pair_id = :currency_pair_id " +
                "  ORDER BY EXORDERS.date_acception DESC, EXORDERS.id DESC " +
                (limit == -1 ? "" : "  LIMIT " + limit);
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("status", 3);
            put("currency_pair_id", currencyPair.getId());
        }};
        return slaveJdbcTemplate.query(sql, params, new RowMapper<OrderAcceptedHistoryDto>() {
            @Override
            public OrderAcceptedHistoryDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                OrderAcceptedHistoryDto orderAcceptedHistoryDto = new OrderAcceptedHistoryDto();
                orderAcceptedHistoryDto.setOrderId(rs.getInt("id"));
                orderAcceptedHistoryDto.setDateAcceptionTime(rs.getTimestamp("date_acception").toLocalDateTime().toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME));
                orderAcceptedHistoryDto.setAcceptionTime(rs.getTimestamp("date_acception"));
                orderAcceptedHistoryDto.setRate(rs.getString("exrate"));
                orderAcceptedHistoryDto.setAmountBase(rs.getString("amount_base"));
                orderAcceptedHistoryDto.setOperationType(OperationType.convert(rs.getInt("operation_type_id")));
                return orderAcceptedHistoryDto;
            }
        });
    }

    @Override
    public List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, LocalDateTime startTime, LocalDateTime endTime, int resolutionValue, String resolutionType) {

        int resolution = resolutionValue;
//        if (resolution == 240 || resolution == 720 || !"MINUTE".equals(resolutionType)) {
//            startTime = startTime.with(LocalTime.MIN);
//            endTime = endTime.with(LocalTime.MIN);
//        }
//
//        LocalDateTime start = startTime.truncatedTo(ChronoUnit.HOURS)
//                .plusMinutes(resolution * (startTime.getMinute() / resolution));
//        LocalDateTime end = endTime.truncatedTo(ChronoUnit.HOURS)
//                .plusMinutes(resolution * (startTime.getMinute() / resolution));

        String startTimeString = startTime.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT_PATTERN));
        String endTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT_PATTERN));
        String sql = "{call GET_DATA_FOR_CANDLE_RANGE(" +
                "STR_TO_DATE(:start_point, '%Y-%m-%d %H:%i:%s'), " +
                "STR_TO_DATE(:end_point, '%Y-%m-%d %H:%i:%s'), " +
                ":step_value, :step_type, :currency_pair_id)}";
        Map<String, Object> params = new HashMap<>();
        params.put("start_point", startTimeString);
        params.put("end_point", endTimeString);
        params.put("step_value", resolutionValue);
        params.put("step_type", resolutionType);
        params.put("currency_pair_id", currencyPair.getId());
        return namedParameterJdbcTemplate.execute(sql, params, ps -> {
            ResultSet rs = ps.executeQuery();
            List<CandleChartItemDto> list = new ArrayList<>();
            while (rs.next()) {
                CandleChartItemDto candleChartItemDto = new CandleChartItemDto();
                candleChartItemDto.setBeginDate(rs.getTimestamp("pred_point"));
                candleChartItemDto.setBeginPeriod(rs.getTimestamp("pred_point").toLocalDateTime());
                candleChartItemDto.setEndDate(rs.getTimestamp("current_point"));
                candleChartItemDto.setEndPeriod(rs.getTimestamp("current_point").toLocalDateTime());
                candleChartItemDto.setOpenRate(rs.getBigDecimal("open_rate"));
                candleChartItemDto.setCloseRate(rs.getBigDecimal("close_rate"));
                candleChartItemDto.setLowRate(rs.getBigDecimal("low_rate"));
                candleChartItemDto.setHighRate(rs.getBigDecimal("high_rate"));
                candleChartItemDto.setBaseVolume(rs.getBigDecimal("base_volume"));
                list.add(candleChartItemDto);
            }
            rs.close();
            return list;
        });
    }

    @Override
    public ExOrderStatisticsDto getOrderStatistic(CurrencyPair currencyPair, BackDealInterval backDealInterval) {
        String sql = "SELECT FIRSTORDER.amount_base AS first_amount_base, FIRSTORDER.exrate AS first_exrate," +
                "            LASTORDER.amount_base AS last_amount_base, LASTORDER.exrate AS last_exrate," +
                "            AGRIGATE.* " +
                "     FROM  " +
                "       (SELECT EXORDERS.currency_pair_id AS currency_pair_id," +
                "       MIN(EXORDERS.date_acception) AS first_date_acception, MAX(EXORDERS.date_acception) AS last_date_acception,  " +
                "       MIN(EXORDERS.exrate) AS min_exrate, MAX(EXORDERS.exrate) AS max_exrate,  " +
                "       SUM(EXORDERS.amount_base) AS deal_sum_base, SUM(EXORDERS.amount_convert) AS deal_sum_convert  " +
                "       FROM EXORDERS  " +
                "       WHERE   " +
                "       EXORDERS.currency_pair_id = :currency_pair_id AND EXORDERS.status_id = :status_id AND   " +
                "       EXORDERS.date_acception >= now() - INTERVAL " + backDealInterval.getInterval() +
                "       GROUP BY currency_pair_id " +
                "       ) AGRIGATE " +
                "     LEFT JOIN EXORDERS FIRSTORDER ON (FIRSTORDER.currency_pair_id = AGRIGATE.currency_pair_id) AND (FIRSTORDER.date_acception = AGRIGATE.first_date_acception)  " +
                "     LEFT JOIN EXORDERS LASTORDER ON (LASTORDER.currency_pair_id = AGRIGATE.currency_pair_id) AND (LASTORDER.date_acception = AGRIGATE.last_date_acception)" +
                " ORDER BY FIRSTORDER.id ASC, LASTORDER.id DESC LIMIT 1 ";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("status_id", String.valueOf(3));
        namedParameters.put("currency_pair_id", String.valueOf(currencyPair.getId()));
        try {
            return slaveJdbcTemplate.queryForObject(sql, namedParameters, new RowMapper<ExOrderStatisticsDto>() {
                @Override
                public ExOrderStatisticsDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ExOrderStatisticsDto exOrderStatisticsDto = new ExOrderStatisticsDto(currencyPair);
                    exOrderStatisticsDto.setFirstOrderAmountBase(rs.getString("first_amount_base"));
                    exOrderStatisticsDto.setFirstOrderRate(rs.getString("first_exrate"));
                    exOrderStatisticsDto.setLastOrderAmountBase(rs.getString("last_amount_base"));
                    exOrderStatisticsDto.setLastOrderRate(rs.getString("last_exrate"));
                    exOrderStatisticsDto.setMinRate(rs.getString("min_exrate"));
                    exOrderStatisticsDto.setMaxRate(rs.getString("max_exrate"));
                    exOrderStatisticsDto.setSumBase(rs.getString("deal_sum_base"));
                    exOrderStatisticsDto.setSumConvert(rs.getString("deal_sum_convert"));
                    return exOrderStatisticsDto;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return new ExOrderStatisticsDto(currencyPair);
        }
    }

    @Override
    public List<OrderWideListDto> getMyOrdersWithState(Integer userId, CurrencyPair currencyPair, OrderStatus status, OperationType operationType, String scope, int offset, int limit, Locale locale) {
        return getMyOrdersWithState(userId, currencyPair, Collections.singletonList(status), operationType, scope, offset, limit, locale);
    }

    @Override
    public List<OrderWideListDto> getMyOrdersWithState(Integer userId, OrderStatus status, CurrencyPair currencyPair, Locale locale,
                                                       String scope, Integer offset, Integer limit, Map<String, String> sortedColumns) {
        String userFilterClause;
        String currencyPairClauseWhere = currencyPair == null ? "" : " AND EXORDERS.currency_pair_id = :currencyPairId ";

        switch (scope) {
            case "ALL":
                userFilterClause = " AND (EXORDERS.user_id = :user_id OR EXORDERS.user_acceptor_id = :user_id) ";
                break;
            case "ACCEPTED":
                userFilterClause = " AND EXORDERS.user_acceptor_id = :user_id ";
                break;
            default:
                userFilterClause = " AND EXORDERS.user_id = :user_id ";
                break;
        }

        List<Integer> operationTypesIds = Arrays.asList(3, 4);
        List<String> sortingRules = sortedColumns
                .entrySet()
                .stream()
                .map(e -> String.format("%s %s", e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        String orderClause = sortingRules.isEmpty()
                ? " ORDER BY date_creation DESC "
                : " ORDER BY " + String.join(", ", sortingRules) + " ";

        String pageClause = "LIMIT ";
        pageClause += limit != 14 ? String.valueOf(limit) : "14";
        pageClause += offset > 0 ? " OFFSET " + String.valueOf(offset) : "";

        String sql = "SELECT EXORDERS.*, CURRENCY_PAIR.name AS currency_pair_name, com.value AS commission_value" +
                "  FROM EXORDERS " +
                " JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = EXORDERS.currency_pair_id)" +
                " INNER JOIN COMMISSION com ON commission_id = com.id  WHERE (status_id = :statusId) " +
                "    AND (operation_type_id IN (:operation_type_id)) " +
                currencyPairClauseWhere +
                userFilterClause +
                orderClause +
                pageClause;
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("user_id", userId);
        namedParameters.put("operation_type_id", operationTypesIds);
        namedParameters.put("statusId", status.getStatus());
        if (currencyPair != null) {
            namedParameters.put("currencyPairId", currencyPair.getId());
        }

        return slaveJdbcTemplate.query(sql, namedParameters, (rs, rowNum) -> {
            OrderWideListDto orderWideListDto = new OrderWideListDto();
            orderWideListDto.setId(rs.getInt("id"));
            orderWideListDto.setUserId(rs.getInt("user_id"));
            orderWideListDto.setOperationTypeEnum(OperationType.convert(rs.getInt("operation_type_id")));
            orderWideListDto.setExExchangeRate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("exrate"), locale, 2));
            orderWideListDto.setAmountBase(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_base"), locale, 2));
            orderWideListDto.setAmountConvert(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_convert"), locale, 2));
            orderWideListDto.setComissionId(rs.getInt("commission_id"));
            orderWideListDto.setCommissionFixedAmount(BigDecimalProcessing.formatLocale(rs.getBigDecimal("commission_fixed_amount"), locale, 2));
            BigDecimal amountWithCommission = rs.getBigDecimal("amount_convert");
            orderWideListDto.setCommissionValue(rs.getDouble("commission_value"));
            if (orderWideListDto.getOperationTypeEnum() == OperationType.SELL) {
                amountWithCommission = BigDecimalProcessing.doAction(amountWithCommission, rs.getBigDecimal("commission_fixed_amount"), ActionType.SUBTRACT);
            } else if (orderWideListDto.getOperationTypeEnum() == OperationType.BUY) {
                amountWithCommission = BigDecimalProcessing.doAction(amountWithCommission, rs.getBigDecimal("commission_fixed_amount"), ActionType.ADD);
            }
            orderWideListDto.setAmountWithCommission(BigDecimalProcessing.formatLocale(amountWithCommission, locale, 2));
            orderWideListDto.setUserAcceptorId(rs.getInt("user_acceptor_id"));
            orderWideListDto.setDateCreation(rs.getTimestamp("date_creation") == null ? null : rs.getTimestamp("date_creation").toLocalDateTime());
            orderWideListDto.setDateAcception(rs.getTimestamp("date_acception") == null ? null : rs.getTimestamp("date_acception").toLocalDateTime());
            orderWideListDto.setStatus(OrderStatus.convert(rs.getInt("status_id")));
            orderWideListDto.setDateStatusModification(rs.getTimestamp("status_modification_date") == null ? null : rs.getTimestamp("status_modification_date").toLocalDateTime());
            orderWideListDto.setCurrencyPairId(rs.getInt("currency_pair_id"));
            orderWideListDto.setCurrencyPairName(rs.getString("currency_pair_name"));
            orderWideListDto.setOrderBaseType(OrderBaseType.valueOf(rs.getString("base_type")));
            orderWideListDto.setOperationType(String.join(" ", orderWideListDto.getOperationTypeEnum().name(), orderWideListDto.getOrderBaseType().name()));
            return orderWideListDto;
        });
    }

    private List<CandleChartItemDto> getCandleChartData(CurrencyPair currencyPair, BackDealInterval backDealInterval, String startTimeSql) {
        String s = "{call GET_DATA_FOR_CANDLE(" + startTimeSql + ", " + backDealInterval.getIntervalValue() + ", '" + backDealInterval.getIntervalType().name() + "', " + currencyPair.getId() + ")}";
        List<CandleChartItemDto> result = namedParameterJdbcTemplate.execute(s, ps -> {
            ResultSet rs = ps.executeQuery();
            List<CandleChartItemDto> list = new ArrayList<>();
            while (rs.next()) {
                CandleChartItemDto candleChartItemDto = new CandleChartItemDto();
                candleChartItemDto.setBeginDate(rs.getTimestamp("pred_point"));
                candleChartItemDto.setBeginPeriod(rs.getTimestamp("pred_point").toLocalDateTime());
                candleChartItemDto.setEndDate(rs.getTimestamp("current_point"));
                candleChartItemDto.setEndPeriod(rs.getTimestamp("current_point").toLocalDateTime());
                candleChartItemDto.setOpenRate(rs.getBigDecimal("open_rate"));
                candleChartItemDto.setCloseRate(rs.getBigDecimal("close_rate"));
                candleChartItemDto.setLowRate(rs.getBigDecimal("low_rate"));
                candleChartItemDto.setHighRate(rs.getBigDecimal("high_rate"));
                candleChartItemDto.setBaseVolume(rs.getBigDecimal("base_volume"));
                list.add(candleChartItemDto);
            }
            rs.close();
            return list;
        });
        return result;
    }

    @Override
    public List<Map<String, Object>> getDataForAreaChart(CurrencyPair currencyPair, BackDealInterval backDealInterval) {
        String sql = "SELECT date_acception, exrate, amount_base FROM EXORDERS " +
                " WHERE status_id=:status_id AND currency_pair_id=:currency_pair_id " +
                " AND date_acception >= now() - INTERVAL " + backDealInterval.getInterval() +
                " ORDER BY date_acception";

        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("status_id", String.valueOf(3));
        namedParameters.put("currency_pair_id", String.valueOf(currencyPair.getId()));
        List<Map<String, Object>> rows = slaveJdbcTemplate.query(sql, namedParameters, (rs, row) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("dateAcception", rs.getTimestamp("date_acception"));
            map.put("exrate", rs.getBigDecimal("exrate"));
            map.put("volume", rs.getBigDecimal("amount_base"));
            return map;
        });

        return rows;
    }

    @Override
    public List<OrderWideListDto> getMyOrdersWithState(Integer userId, CurrencyPair currencyPair, List<OrderStatus> statuses,
                                                       OperationType operationType,
                                                       String scope, Integer offset, Integer limit, Locale locale) {
        String userFilterClause;
        String joinClause = "";

        if (scope == null || scope.isEmpty()) {
            scope = "OTHER";
        }

        switch (scope) {
            case "ALL":
                userFilterClause = " AND (EXORDERS.user_id = :user_id OR EXORDERS.user_acceptor_id = :user_id) ";
                joinClause = " LEFT JOIN EXORDERS EX2 ON EXORDERS.id = EX2.counter_order_id ";
                break;
            case "ACCEPTED":
                userFilterClause = " AND EXORDERS.user_acceptor_id = :user_id ";
                break;
            default:
                userFilterClause = " AND EXORDERS.user_id = :user_id ";
                break;
        }

        List<Integer> statusIds = statuses.stream().map(OrderStatus::getStatus).collect(Collectors.toList());
        List<Integer> operationTypesIds = Arrays.asList(3, 4);

        String orderClause = "  ORDER BY -date_acception ASC, date_creation DESC";
        if (statusIds.size() > 1) {
            orderClause = "  ORDER BY status_modification_date DESC";
        }
        String sql = "SELECT EXORDERS.*, CURRENCY_PAIR.name AS currency_pair_name" +
                "  FROM EXORDERS " +
                "  JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = EXORDERS.currency_pair_id) " +
                "  WHERE (status_id IN (:status_ids))" +
                "    AND (operation_type_id IN (:operation_type_id))" +
                (currencyPair == null ? "" : " AND EXORDERS.currency_pair_id=" + currencyPair.getId()) +
                userFilterClause +
                orderClause +
                (limit == -1 ? "" : "  LIMIT " + limit + " OFFSET " + offset);
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("user_id", userId);
        namedParameters.put("status_ids", statusIds);
        if (operationType != null) {
            namedParameters.put("operation_type_id", operationType.getType());
        } else {
            namedParameters.put("operation_type_id", operationTypesIds);
        }
        return slaveJdbcTemplate.query(sql, namedParameters, new RowMapper<OrderWideListDto>() {
            @Override
            public OrderWideListDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                OrderWideListDto orderWideListDto = new OrderWideListDto();
                orderWideListDto.setId(rs.getInt("id"));
                orderWideListDto.setUserId(rs.getInt("user_id"));
                orderWideListDto.setOperationTypeEnum(OperationType.convert(rs.getInt("operation_type_id")));
                orderWideListDto.setExExchangeRate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("exrate"), locale, 2));
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
                orderWideListDto.setUserAcceptorId(rs.getInt("user_acceptor_id"));
                orderWideListDto.setDateCreation(rs.getTimestamp("date_creation") == null ? null : rs.getTimestamp("date_creation").toLocalDateTime());
                orderWideListDto.setDateAcception(rs.getTimestamp("date_acception") == null ? null : rs.getTimestamp("date_acception").toLocalDateTime());
                orderWideListDto.setStatus(OrderStatus.convert(rs.getInt("status_id")));
                orderWideListDto.setDateStatusModification(rs.getTimestamp("status_modification_date") == null ? null : rs.getTimestamp("status_modification_date").toLocalDateTime());
                orderWideListDto.setCurrencyPairId(rs.getInt("currency_pair_id"));
                orderWideListDto.setCurrencyPairName(rs.getString("currency_pair_name"));
                orderWideListDto.setOrderBaseType(OrderBaseType.valueOf(rs.getString("base_type")));
                orderWideListDto.setOperationType(String.join(" ", orderWideListDto.getOperationTypeEnum().name(), orderWideListDto.getOrderBaseType().name()));
                return orderWideListDto;
            }
        });
    }

    @Override
    public OrderCommissionsDto getCommissionForOrder(UserRole userRole) {
        final String sql =
                "  SELECT SUM(sell_commission) as sell_commission, SUM(buy_commission) as buy_commission " +
                        "  FROM " +
                        "      ((SELECT SELL.value as sell_commission, 0 as buy_commission " +
                        "      FROM COMMISSION SELL " +
                        "      WHERE operation_type = 3 AND user_role = :user_role " +
                        "      ORDER BY date DESC LIMIT 1)  " +
                        "    UNION " +
                        "      (SELECT 0, BUY.value " +
                        "      FROM COMMISSION BUY " +
                        "      WHERE operation_type = 4 AND user_role = :user_role " +
                        "      ORDER BY date DESC LIMIT 1) " +
                        "  ) COMMISSION";
        try {
            Map<String, Integer> params = Collections.singletonMap("user_role", userRole.getRole());
            return namedParameterJdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
                OrderCommissionsDto orderCommissionsDto = new OrderCommissionsDto();
                orderCommissionsDto.setSellCommission(rs.getBigDecimal("sell_commission"));
                orderCommissionsDto.setBuyCommission(rs.getBigDecimal("buy_commission"));
                return orderCommissionsDto;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<StatisticForMarket> getOrderStatisticForNewMarkets() {

        String sql = "SELECT" +
                "  RESULT.currency_pair_name," +
                "  RESULT.market," +
                "  RESULT.currency_pair_id," +
                "  RESULT.last_exrate," +
                "  RESULT.pred_last_exrate," +
                "  RESULT.volume," +
                "  RESULT.type" +
                " FROM" +
                "  ((SELECT" +
                "      CURRENCY_PAIR.name          AS currency_pair_name," +
                "      CURRENCY_PAIR.market        AS market," +
                "      CURRENCY_PAIR.id            AS currency_pair_id," +
                "      CURRENCY_PAIR.type                      AS type," +
                "      (SELECT SUM(EX.amount_base)" +
                "       FROM EXORDERS EX" +
                "       WHERE" +
                "         (EX.currency_pair_id = AGRIGATE.currency_pair_id) AND" +
                "         (EX.status_id = AGRIGATE.status_id) AND (EX.date_creation >= NOW() - INTERVAL 24 HOUR)) AS volume," +
                "      (SELECT LASTORDER.exrate" +
                "       FROM EXORDERS LASTORDER" +
                "       WHERE" +
                "         (LASTORDER.currency_pair_id = AGRIGATE.currency_pair_id) AND" +
                "         (LASTORDER.status_id = AGRIGATE.status_id)" +
                "       ORDER BY LASTORDER.date_acception DESC, LASTORDER.id DESC" +
                "       LIMIT 1)  AS last_exrate," +
                "      (SELECT PRED_LASTORDER.exrate" +
                "       FROM EXORDERS PRED_LASTORDER" +
                "       WHERE" +
                "         (PRED_LASTORDER.currency_pair_id = AGRIGATE.currency_pair_id) AND" +
                "         (PRED_LASTORDER.status_id = AGRIGATE.status_id) AND" +
                "         (PRED_LASTORDER.date_creation >= NOW() - INTERVAL 24 HOUR)" +
                "       ORDER BY PRED_LASTORDER.date_acception ASC, PRED_LASTORDER.id DESC" +
                "       LIMIT 1)  AS pred_last_exrate" +
                "    FROM (" +
                "           SELECT DISTINCT" +
                "             EXORDERS.status_id        AS status_id," +
                "             EXORDERS.currency_pair_id AS currency_pair_id" +
                "           FROM EXORDERS" +
                "           WHERE EXORDERS.status_id = :status_id" +
                "         )" +
                "         AGRIGATE" +
                "      JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = AGRIGATE.currency_pair_id) AND (CURRENCY_PAIR.hidden != 1)" +
                "    ORDER BY -CURRENCY_PAIR.pair_order DESC)" +
                "   UNION ALL (" +
                "     SELECT" +
                "       CP.name   AS currency_pair_name," +
                "       CP.market AS market," +
                "       CP.id     AS currency_pair_id," +
                "       CP.type   AS type," +
                "       0         AS volume," +
                "       0         AS last_exrate," +
                "       0         AS pred_last_exrate" +
                "     FROM CURRENCY_PAIR CP" +
                "     WHERE CP.id NOT IN (SELECT DISTINCT EXORDERS.currency_pair_id AS currency_pair_id" +
                "                         FROM EXORDERS" +
                "                         WHERE EXORDERS.status_id = :status_id) AND CP.hidden = 0" +
                "   )) RESULT";

        Map<String, Object> params = new HashMap<>();
        params.put("status_id", 3);

        return namedParameterJdbcTemplate.query(sql, params, (rs, row) -> {
            StatisticForMarket statisticForMarket = new StatisticForMarket();

            statisticForMarket.setCurrencyPairId(rs.getInt("currency_pair_id"));
            statisticForMarket.setCurrencyPairName(rs.getString("currency_pair_name"));
            statisticForMarket.setMarket(rs.getString("market"));
            statisticForMarket.setLastOrderRate(rs.getBigDecimal("last_exrate"));
            statisticForMarket.setPredLastOrderRate(rs.getBigDecimal("pred_last_exrate"));
            if (rs.getObject("volume") != null) {
                statisticForMarket.setVolume(rs.getBigDecimal("volume"));
            } else {
                statisticForMarket.setVolume(BigDecimal.ZERO);
            }
            statisticForMarket.setType(CurrencyPairType.valueOf(rs.getString("type")));
            return statisticForMarket;
        });
    }

    @Override
    public Optional<BigDecimal> getLastOrderPriceByCurrencyPair(int currencyPairId) {
        String sql = "SELECT exrate FROM EXORDERS WHERE status_id = 3 AND currency_pair_id = :currency_pair_id AND operation_type_id in (3,4) " +
                "ORDER BY date_acception DESC, id DESC LIMIT 1";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", currencyPairId);
        try {
            return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, namedParameters, BigDecimal.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<OrderListDto> findAllByOrderTypeAndCurrencyId(OrderType orderType, Integer currencyId) {
        String sql = "SELECT id, currency_pair_id, operation_type_id, exrate, amount_base, " +
                " amount_convert, commission_fixed_amount, date_creation, date_acception" +
                "  FROM EXORDERS " +
                "  WHERE status_id = 2 AND operation_type_id = :operationTypeId AND currency_pair_id=:currency_pair_id" +
//                "  AND date_creation >= (DATE_SUB(CURDATE(), INTERVAL 10 DAY))" +
                "  ORDER BY exrate ASC";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", currencyId);
        namedParameters.put("operationTypeId", orderType.getOperationType().getType());
        return slaveJdbcTemplate.query(sql, namedParameters, openOrderListDtoRowMapper());
    }


    @Override
    public Integer getMyOrdersWithStateCount(int userId, CurrencyPair currencyPair, OrderStatus status, String scope, Integer offset, Integer limit, Locale locale) {
        String userFilterClause;
        String currencyPairClauseJoin = currencyPair == null ? "" : "  JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = EXORDERS.currency_pair_id) ";
        String currencyPairClauseWhere = currencyPair == null ? "" : "    AND EXORDERS.currency_pair_id = :currencyPairId ";

        switch (scope) {
            case "ALL":
                userFilterClause = " AND (EXORDERS.user_id = :user_id OR EXORDERS.user_acceptor_id = :user_id) ";
                break;
            case "ACCEPTED":
                userFilterClause = " AND EXORDERS.user_acceptor_id = :user_id ";
                break;
            default:
                userFilterClause = " AND EXORDERS.user_id = :user_id ";
                break;
        }

        List<Integer> operationTypesIds = Arrays.asList(3, 4);

        String sql = "SELECT COUNT(*) " +
                "  FROM EXORDERS " +
                currencyPairClauseJoin +
                "  WHERE (status_id = :statusId) " +
                "    AND (operation_type_id IN (:operation_type_id)) " +
                currencyPairClauseWhere +
                userFilterClause;
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("user_id", userId);
        namedParameters.put("statusId", status.getStatus());
        namedParameters.put("operation_type_id", operationTypesIds);
        if (currencyPair != null) {
            namedParameters.put("currencyPairId", currencyPair.getId());
        }
        return slaveJdbcTemplate.queryForObject(sql, namedParameters, Integer.TYPE);
    }

    @Override
    public OrderRoleInfoForDelete getOrderRoleInfo(int orderId) {
        String sql = "SELECT EO.status_id, CREATOR.roleid AS creator_role, ACCEPTOR.roleid AS acceptor_role, COUNT(TX.id) AS tx_count from EXORDERS EO " +
                "  JOIN USER CREATOR ON EO.user_id = CREATOR.id " +
                "  LEFT JOIN USER ACCEPTOR ON EO.user_acceptor_id = ACCEPTOR.id " +
                // join on source type and source id to use index
                "  LEFT JOIN TRANSACTION TX ON TX.source_type = 'ORDER' AND TX.source_id = EO.id " +
                "WHERE EO.id = :order_id;";
        return namedParameterJdbcTemplate.queryForObject(sql, Collections.singletonMap("order_id", orderId), (rs, rowNum) -> {
            Integer statusId = getInteger(rs, "status_id");
            Integer creatorRoleId = getInteger(rs, "creator_role");
            Integer acceptorRoleId = getInteger(rs, "acceptor_role");
            OrderStatus status = statusId == null ? null : OrderStatus.convert(statusId);
            UserRole creatorRole = creatorRoleId == null ? null : UserRole.convert(creatorRoleId);
            UserRole acceptorRole = acceptorRoleId == null ? null : UserRole.convert(acceptorRoleId);
            int txCount = rs.getInt("tx_count");
            return new OrderRoleInfoForDelete(status, creatorRole, acceptorRole, txCount);
        });
    }

    private Integer getInteger(ResultSet rs, String fieldName) throws SQLException {
        Integer result = rs.getInt(fieldName);
        if (rs.wasNull()) {
            result = null;
        }
        return result;
    }

    private RowMapper<OrderListDto> openOrderListDtoRowMapper(){
        return (rs, rowNum) -> {
            OrderListDto order = new OrderListDto();
            order.setId(rs.getInt("id"));
            order.setOrderType(OperationType.convert(rs.getInt("operation_type_id")));
            order.setExrate(rs.getString("exrate"));
            order.setAmountBase(rs.getString("amount_base"));
            order.setCreated(convertTimeStampToLocalDateTime(rs,"date_creation"));
            return order;
        };
    }

    private LocalDateTime convertTimeStampToLocalDateTime(ResultSet rs, String columnName) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(columnName);
        if (timestamp == null) {
            return null;
        }
        return timestamp.toLocalDateTime();
    }

}