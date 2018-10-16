package me.exrates.cache;

import me.exrates.dao.OrderDao;
import me.exrates.model.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ExchangeRatesHolderImpl implements ExchangeRatesHolder {

    private Map<Integer, ExOrderStatisticsShortByPairsDto> ratesMap = new ConcurrentHashMap<>();

    private final OrderDao orderDao;

    @Autowired
    public ExchangeRatesHolderImpl(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @PostConstruct
    private void init() {
        List<ExOrderStatisticsShortByPairsDto> list = orderDao.getOrderStatisticByPairs();
        list.forEach(p -> ratesMap.put(p.getCurrencyPairId(), p));
    }

    @Override
    public List<ExOrderStatisticsShortByPairsDto> getAllRates() {
        return new ArrayList<>(ratesMap.values());
    }

    @Override
    public List<ExOrderStatisticsShortByPairsDto> getCurrenciesRates(List<Integer> id) {
        if (id == null || id.isEmpty()) {
            return Collections.emptyList();
        }
        List<ExOrderStatisticsShortByPairsDto> result = new ArrayList<>();
        id.forEach(p -> result.add(ratesMap.get(p)));
        return result;
    }
}
