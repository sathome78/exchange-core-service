package me.exrates.service.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.exception.CurrencyPairNotFoundException;
import me.exrates.model.dto.CurrencyPairLimitDto;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.main.Currency;
import me.exrates.model.main.CurrencyPair;
import me.exrates.service.CurrencyService;
import me.exrates.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.exrates.dao.impl.CurrencyDaoImpl.currencyPairRowShort;

@Repository
public class CurrencyServiceImpl implements CurrencyService {

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private CurrencyDao currencyDao;
    //    @Autowired
//    private UserService userService;
    @Autowired
    UserRoleService userRoleService;

    public List<CurrencyPair> getAllCurrencyPairs(CurrencyPairType type) {
        return currencyDao.getAllCurrencyPairs(type);
    }

    @Override
    public List<CurrencyPair> getAllCurrencyPairsInAlphabeticOrder(CurrencyPairType type) {
        List<CurrencyPair> result = currencyDao.getAllCurrencyPairs(type);
        result.sort(Comparator.comparing(CurrencyPair::getName));
        return result;
    }

    @Override
    public CurrencyPair findCurrencyPairById(int currencyPairId) {
        try {
            return currencyDao.findCurrencyPairById(currencyPairId);
        } catch (EmptyResultDataAccessException e) {
            throw new CurrencyPairNotFoundException("Currency pair not found");
        }
    }
    //TODO should be fix.
    @Override
    public CurrencyPairLimitDto findLimitForRoleByCurrencyPairAndType(int currencyPairId, OperationType operationType) {
//        UserRole userRole = userService.getUserRoleFromSecurityContext();
//        OrderType orderType = OrderType.convert(operationType.name());
//        return currencyDao.findCurrencyPairLimitForRoleByPairAndType(currencyPairId, userRole.getRole(), orderType.getType());
        return null;
    }


    public List<Currency> findAllCurrenciesWithHidden() {
        return currencyDao.findAllCurrenciesWithHidden();
    }

    @Override
    public CurrencyPair getCurrencyPairByName(String currencyPair) {
        return currencyDao.findCurrencyPairByName(currencyPair);
    }

    public List<CurrencyPair> findPermitedCurrencyPairs(CurrencyPairType currencyPairType) {
        return currencyDao.findPermitedCurrencyPairs(currencyPairType);
    }

    @Override
    public Currency findByName(String name) {
        return currencyDao.findByName(name);
    }

    @Override
    public List<CurrencyPair> getPairsByFirstPartName(String partName) {
        return currencyDao.findAllCurrenciesByFirstPartName(partName);
    }

    @Override
    public List<CurrencyPair> getPairsBySecondPartName(String partName) {
        return currencyDao.findAllCurrenciesBySecondPartName(partName);
    }

    @Override
    public List<CurrencyPair> findAllCurrenciesBySecondPartName(String partName) {
        final String sql = "SELECT * FROM CURRENCY_PAIR WHERE name LIKE CONCAT('%/', :part) AND hidden = 0 order by name";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("part", partName.toUpperCase());
        }};
        return jdbcTemplate.query(sql, params, currencyPairRowShort);
    }

    @Override
    public CurrencyPair getNotHiddenCurrencyPairByName(String currencyPair) {
        return currencyDao.getNotHiddenCurrencyPairByName(currencyPair);
    }


}
