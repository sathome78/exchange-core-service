package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.NewsDao;
import me.exrates.dao.OrderDao;
import me.exrates.model.dto.CandleChartItemDto;
import me.exrates.model.dto.ExOrderStatisticsDto;
import me.exrates.model.enums.UserRole;
import me.exrates.model.main.BackDealInterval;
import me.exrates.model.main.CurrencyPair;
import me.exrates.model.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.onlineTableDto.NewsDto;
import me.exrates.model.onlineTableDto.OrderAcceptedHistoryDto;
import me.exrates.model.onlineTableDto.OrderListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Log4j2
public class ServiceCacheableProxy {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private NewsDao newsDao;

    @Autowired
    private Twitter twitter;


    @CacheEvict(cacheNames = "orderBuy", key = "#currencyPair.id", condition = "#evictCache", beforeInvocation = true)
    @Cacheable(cacheNames = "orderBuy", key = "#currencyPair.id")
    public List<OrderListDto> getAllBuyOrders(
            CurrencyPair currencyPair,
            UserRole filterRole, Boolean evictCache) {
        log.debug(String.format("\n%s evictCache: %s", currencyPair, evictCache));
        return orderDao.getOrdersBuyForCurrencyPair(currencyPair, filterRole);
    }

    @CacheEvict(cacheNames = "orderSell", key = "#currencyPair.id", condition = "#evictCache", beforeInvocation = true)
    @Cacheable(cacheNames = "orderSell", key = "#currencyPair.id")
    public List<OrderListDto> getAllSellOrders(
            CurrencyPair currencyPair,
            UserRole filterRole, Boolean evictCache) {
        log.debug(String.format("\n%s evictCache: %s", currencyPair, evictCache));
        return orderDao.getOrdersSellForCurrencyPair(currencyPair, filterRole);
    }

    @Cacheable(cacheNames = "newsBrief", key = "#locale")
    public List<NewsDto> getNewsBriefList(Integer offset, Integer limit, Locale locale) {
        log.debug(String.format("\nlocale: %s", locale));
        return newsDao.getNewsBriefList(offset, limit, locale);


    }


    @Transactional(propagation = Propagation.NEVER)
    @Cacheable(cacheNames = "twitter", key = "#size")
    public List<Tweet> getTwitterTimeline(Integer size) {
        return twitter.timelineOperations().getUserTimeline(size);
    }
}
