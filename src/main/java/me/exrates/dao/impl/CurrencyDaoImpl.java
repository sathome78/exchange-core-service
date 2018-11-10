package me.exrates.dao.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.model.enums.CurrencyPairType;
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
        return jdbcTemplate.query(sql, Collections.singletonMap("type", currencyPairType.name()), currencyPairRowMapper);
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
