package me.exrates.dao.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.model.dto.CurrencyPairLimitDto;
import me.exrates.model.dto.UserCurrencyOperationPermissionDto;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.InvoiceOperationDirection;
import me.exrates.model.enums.InvoiceOperationPermission;
import me.exrates.model.main.Currency;
import me.exrates.model.main.CurrencyPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CurrencyDaoImpl implements CurrencyDao {

    protected static RowMapper<CurrencyPair> currencyPairRowMapper = (rs, row) -> {
        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setId(rs.getInt("id"));
        currencyPair.setName(rs.getString("name"));
        currencyPair.setPairType(CurrencyPairType.valueOf(rs.getString("type")));
        /**/
        Currency currency1 = new Currency();
        currency1.setId(rs.getInt("currency1_id"));
        currency1.setName(rs.getString("currency1_name"));
        currencyPair.setCurrency1(currency1);
        /**/
        Currency currency2 = new Currency();
        currency2.setId(rs.getInt("currency2_id"));
        currency2.setName(rs.getString("currency2_name"));
        currencyPair.setCurrency2(currency2);
        /**/
        currencyPair.setMarket(rs.getString("market"));

        return currencyPair;

    };


    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public List<UserCurrencyOperationPermissionDto> findCurrencyOperationPermittedByUserList(Integer userId) {
        String sql = "SELECT CUR.id, CUR.name, IOP.invoice_operation_permission_id, IOP.operation_direction " +
                " FROM CURRENCY CUR " +
                " JOIN USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP ON " +
                "				(IOP.currency_id=CUR.id) " +
                "				AND (IOP.user_id=:user_id) " +
                " WHERE CUR.hidden IS NOT TRUE " +
                " ORDER BY CUR.id ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("user_id", userId);
        }};
        return jdbcTemplate.query(sql, params, (rs, row) -> {
            UserCurrencyOperationPermissionDto dto = new UserCurrencyOperationPermissionDto();
            dto.setUserId(userId);
            dto.setCurrencyId(rs.getInt("id"));
            dto.setCurrencyName(rs.getString("name"));
            dto.setInvoiceOperationDirection(InvoiceOperationDirection.valueOf(rs.getString("operation_direction")));
            Integer permissionCode = rs.getObject("invoice_operation_permission_id") == null ? 0 : (Integer) rs.getObject("invoice_operation_permission_id");
            dto.setInvoiceOperationPermission(InvoiceOperationPermission.convert(permissionCode));
            return dto;
        });

    }

    @Override
    public CurrencyPairLimitDto findCurrencyPairLimitForRoleByPairAndType(Integer currencyPairId, Integer roleId, Integer orderTypeId) {
        String sql = "SELECT CURRENCY_PAIR.id AS currency_pair_id, CURRENCY_PAIR.name AS currency_pair_name, lim.min_rate, lim.max_rate, " +
                "lim.min_amount, lim.max_amount " +
                " FROM CURRENCY_PAIR_LIMIT lim " +
                " JOIN CURRENCY_PAIR ON lim.currency_pair_id = CURRENCY_PAIR.id AND CURRENCY_PAIR.hidden != 1 " +
                " WHERE lim.currency_pair_id = :currency_pair_id AND lim.user_role_id = :user_role_id AND lim.order_type_id = :order_type_id";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", currencyPairId);
        namedParameters.put("user_role_id", roleId);
        namedParameters.put("order_type_id", orderTypeId);
        return jdbcTemplate.queryForObject(sql, namedParameters, (rs, rowNum) -> {
            CurrencyPairLimitDto dto = new CurrencyPairLimitDto();
            dto.setCurrencyPairId(rs.getInt("currency_pair_id"));
            dto.setCurrencyPairName(rs.getString("currency_pair_name"));
            dto.setMinRate(rs.getBigDecimal("min_rate"));
            dto.setMaxRate(rs.getBigDecimal("max_rate"));
            dto.setMinAmount(rs.getBigDecimal("min_amount"));
            dto.setMaxAmount(rs.getBigDecimal("max_amount"));
            return dto;
        });

    }

    @Override
    public CurrencyPair getNotHiddenCurrencyPairByName(String currencyPairName) {
        String sql = "SELECT id, currency1_id, currency2_id, name, market, type," +
                "(select name from CURRENCY where id = currency1_id) as currency1_name, " +
                "(select name from CURRENCY where id = currency2_id) as currency2_name " +
                " FROM CURRENCY_PAIR WHERE name = :currencyPairName AND hidden IS NOT TRUE ";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("currencyPairName", String.valueOf(currencyPairName));
        return jdbcTemplate.queryForObject(sql, namedParameters, currencyPairRowMapper);
    }

    @Override
    public List<CurrencyPair> getAllCurrencyPairs(CurrencyPairType type) {
        String typeClause = "";
        if (type != null && type != CurrencyPairType.ALL) {
            typeClause = " AND type =:pairType ";
        }
        String sql = "SELECT id, currency1_id, currency2_id, name, market, type, " +
                "(select name from CURRENCY where id = currency1_id) as currency1_name, " +
                "(select name from CURRENCY where id = currency2_id) as currency2_name " +
                " FROM CURRENCY_PAIR " +
                " WHERE hidden IS NOT TRUE " + typeClause +
                " ORDER BY -pair_order DESC";
        return jdbcTemplate.query(sql, Collections.singletonMap("pairType", type.name()), currencyPairRowMapper);
    }

    public List<Currency> getCurrList() {
        String sql = "SELECT id, name FROM CURRENCY WHERE hidden IS NOT TRUE ";
        List<Currency> currList;
        currList = jdbcTemplate.query(sql, (rs, row) -> {
            Currency currency = new Currency();
            currency.setId(rs.getInt("id"));
            currency.setName(rs.getString("name"));
            return currency;

        });
        return currList;
    }

    @Override
    public Currency findById(int id) {
        final String sql = "SELECT * FROM CURRENCY WHERE id = :id";
        final Map<String, Integer> params = new HashMap<String, Integer>() {
            {
                put("id", id);
            }
        };
        return jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Currency.class));

    }

    @Override
    public CurrencyPair findCurrencyPairById(int currencyPairId) {
        String sql = "SELECT id, currency1_id, currency2_id, name, market, type," +
                "(select name from CURRENCY where id = currency1_id) as currency1_name, " +
                "(select name from CURRENCY where id = currency2_id) as currency2_name " +
                " FROM CURRENCY_PAIR WHERE id = :currencyPairId";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("currencyPairId", String.valueOf(currencyPairId));
        return jdbcTemplate.queryForObject(sql, namedParameters, currencyPairRowMapper);
    }

    @Override
    public CurrencyPair findCurrencyPairByName(String currencyPair) {
        String sql = "SELECT cp.id, " +
                "cp.currency1_id, " +
                "cp.currency2_id, " +
                "cp.name, " +
                "cp.market, " +
                "cp.type," +
                "(select name from CURRENCY where id = currency1_id) as currency1_name, " +
                "(select name from CURRENCY where id = currency2_id) as currency2_name " +
                " FROM CURRENCY_PAIR cp" +
                " WHERE cp.name = :currency_pair";

        Map<String, String> params = new HashMap<>();
        params.put("currency_pair", currencyPair);

        return jdbcTemplate.queryForObject(sql, params, currencyPairRowMapper);
    }

    @Override
    public List<CurrencyPair> findPermitedCurrencyPairs(CurrencyPairType currencyPairType) {


        String sql = "SELECT id, currency1_id, currency2_id, name, market, type, " +
                "        (select name from CURRENCY where id = currency1_id) as currency1_name, " +
                "        (select name from CURRENCY where id = currency2_id) as currency2_name " +
                "         FROM CURRENCY_PAIR " +
                "         WHERE hidden IS NOT TRUE AND permitted_link IS TRUE ";
        if (currencyPairType != CurrencyPairType.ALL) {
            sql = sql.concat(" AND type =:type");
        }
        return jdbcTemplate.query(sql, Collections.singletonMap("type", currencyPairType.name()),currencyPairRowMapper);
    }

    @Override
    public List<Currency> findAllCurrenciesWithHidden() {

        final String sql = "SELECT * FROM CURRENCY";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Currency.class));
    }

    @Override
    public Currency findByName(String name) {
        final String sql = "SELECT * FROM CURRENCY WHERE name = :name";
        final Map<String, String> params = new HashMap<String, String>() {
            {
                put("name", name);
            }
        };
        return jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Currency.class));
    }

    @Override
    public CurrencyPair findCurrencyPairByOrderId(Integer orderId) {

        String sql = "SELECT CURRENCY_PAIR.id, CURRENCY_PAIR.currency1_id, CURRENCY_PAIR.currency2_id, name, type," +
                "CURRENCY_PAIR.market, " +
                "(select name from CURRENCY where id = currency1_id) as currency1_name, " +
                "(select name from CURRENCY where id = currency2_id) as currency2_name " +
                " FROM EXORDERS " +
                " JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = EXORDERS.currency_pair_id) " +
                " WHERE EXORDERS.id = :order_id";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("order_id", String.valueOf(orderId));
        return jdbcTemplate.queryForObject(sql, namedParameters, currencyPairRowMapper);

    }
}
