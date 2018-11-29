package me.exrates.controller.ngcontroller.dao.impl;

import me.exrates.controller.ngcontroller.dao.BalanceDao;
import me.exrates.controller.ngcontroller.model.UserBalancesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BalanceDaoImpl implements BalanceDao {

    public static final String GET_BALANCE = "SELECT WALLET.currency_id,\n" +
            "       WALLET.active_balance,\n" +
            "       WALLET.reserved_balance,\n" +
            "       C.name,\n" +
            "       C.description,\n" +
            "       C2.usd_rate,\n" +
            "       M.description,\n" +
            "       M.process_type,\n" +
            "       (WALLET.active_balance * C2.usd_rate)   as active_balance_in_usd,\n" +
            "       (WALLET.reserved_balance * C2.usd_rate) as reserved_balance_in_usd\n" +
            "FROM WALLET\n" +
            "       JOIN CURRENCY C on WALLET.currency_id = C.id\n" +
            "       JOIN COMPANY_EXTERNAL_WALLET_BALANCES C2 on C.id = C2.currency_id\n" +
            "       JOIN MERCHANT_CURRENCY MC on MC.currency_id = WALLET.currency_id\n" +
            "       JOIN MERCHANT M on M.id = MC.merchant_id\n" +
            "WHERE user_id = :userId\n AND C.name like :tickerName \n" +
            "ORDER BY C.name DESC LIMIT :limit OFFSET :offset";

    @Autowired
    @Qualifier(value = "slaveTemplate")
    private NamedParameterJdbcTemplate slaveJdbcTemplate;

    public List<UserBalancesDto> getUserBalances(String tikerName, String sortByCreated, Integer page, Integer limit, int userId) {
        int offset = page > 1 ? page * limit : 0;
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("limit", limit);
        sqlParameterSource.addValue("offset", offset);
        sqlParameterSource.addValue("userId", userId);
        sqlParameterSource.addValue("tickerName", "%" + tikerName + "%");
        return slaveJdbcTemplate.query(GET_BALANCE, sqlParameterSource, UserBalancesDto.builder().build());
    }
}