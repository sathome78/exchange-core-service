package me.exrates.controller;

import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;
import me.exrates.cache.ExchangeRatesHolder;
import me.exrates.model.dto.*;
import me.exrates.model.enums.*;
import me.exrates.model.main.BackDealInterval;
import me.exrates.model.main.CacheData;
import me.exrates.model.main.CurrencyPair;
import me.exrates.model.onlineTableDto.*;
import me.exrates.model.onlineTableDto.NewsDto;
import me.exrates.service.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.core.util.Assert.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * The controller contains online methods. "Online method" is the handler of online requests,
 * which updates data on browser page in online mode.
 * The online request is the automatic request and is not sign of user activity and should not update
 * session param "sessionEndTime", which stores the time of end the current session.
 * Another (not online) methods, excluding resources request, reset session param "sessionEndTime" and session life starts from begin
 * Updating session param "sessionEndTime" happens in class XssRequestFilter.
 * <p>
 * IMPORTANT!
 * The OnlineRestController can contain not online methods. But all online methods must be placed in the OnlineRestController
 * Online methods must be annotated with @OnlineMethod
 *
 * @author ValkSam
 */
@Log4j2
@PropertySource("classpath:session.properties")
@RestController
public class OnlineRestController {
    private static final Logger LOGGER = LogManager.getLogger(OnlineRestController.class);
    /* if SESSION_LIFETIME_HARD set, session will be killed after time expired, regardless of activity the session
    set SESSION_LIFETIME_HARD = 0 to ignore it*/
    /* public static final long SESSION_LIFETIME_HARD = Math.round(90 * 60); //SECONDS*/
    /* if SESSION_LIFETIME_INACTIVE set, session will be killed if it is inactive during the time
     * set SESSION_LIFETIME_INACTIVE = 0 to ignore it and session lifetime will be set to default value (30 mins)
     * The time of end the current session is stored in session param "sessionEndTime", which calculated in millisec as
     * new Date().getTime() + SESSION_LIFETIME_HARD * 1000*/
    /*public static final int SESSION_LIFETIME_INACTIVE = 0; //SECONDS*/
    /*default depth the interval for chart*/
    final public static BackDealInterval BACK_DEAL_INTERVAL_DEFAULT = new BackDealInterval("24 HOUR");
    /*depth the accepted order history*/
    final public static BackDealInterval ORDER_HISTORY_INTERVAL = new BackDealInterval("24 HOUR");
    /*limit the data fetching of order history (additional to ORDER_HISTORY_INTERVAL). (-1) means no limit*/
    final public static Integer ORDER_HISTORY_LIMIT = 100;
    /*default limit the data fetching for all tables. (-1) means no limit*/
    final public static Integer TABLES_LIMIT_DEFAULT = -1;
    /*default type of the chart*/
    final public static ChartType CHART_TYPE_DEFAULT = ChartType.STOCK;
    /*it's need to install only one: SESSION_LIFETIME_HARD or SESSION_LIFETIME_INACTIVE*/

    private @Value("${session.timeParamName}")
    String sessionTimeMinutes;

    private @Value("${session.lastRequestParamName}")
    String sessionLastRequestParamName;

    @Autowired
    CommissionService commissionService;

    @Autowired
    OrderService orderService;

    @Autowired
    WalletService walletService;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    NewsService newsService;

    @Autowired
    ReferralService referralService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    private UserService userService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    LocaleResolver localeResolver;

    @Autowired
    InputOutputService inputOutputService;

    @Autowired
    StopOrderService stopOrderService;

    @Autowired
    private ExchangeRatesHolder exchangeRatesHolder;

    @RequestMapping(value = "/dashboard/commission/{type}", method = RequestMethod.GET)
    public BigDecimal getCommissions(@PathVariable("type") String type) {
        UserRole userRole = userService.getUserRoleFromSecurityContext();
        try {
            switch (type) {
                case "sell":
                    return commissionService.findCommissionByTypeAndRole(OperationType.SELL, userRole).getValue();
                case "buy":
                    return commissionService.findCommissionByTypeAndRole(OperationType.BUY, userRole).getValue();
                default:
                    return null;
            }
        } finally {
        }
    }


    @RequestMapping(value = "/dashboard/myWalletsStatistic", method = RequestMethod.GET)
    public Map<String, Object> getMyWalletsStatisticsForAllCurrencies(@RequestParam(required = false) Boolean refreshIfNeeded,
                                                                      @RequestParam(defaultValue = "MAIN") CurrencyPairType type,
                                                                      Principal principal, HttpServletRequest request) {
        if (principal == null) {
            return null;
        }
        String email = principal.getName();
        String cacheKey = "myWalletsStatistic" + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        List<MyWalletsStatisticsDto> resultWallet = walletService.getAllWalletsForUserReduced(cacheData, email, localeResolver.resolveLocale(request), type);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("mapWallets", resultWallet);

        if (resultWallet.size() > 1) {

            List<ExOrderStatisticsShortByPairsDto> resultOrders = exchangeRatesHolder.getAllRates();

            final HashMap<String, BigDecimal> ratesBTC_ETH = new HashMap<>();
            resultOrders.stream()
                    .filter(p -> p.getCurrencyPairName().contains("BTC/USD") || p.getCurrencyPairName().contains("ETH/USD"))
                    .forEach(p -> ratesBTC_ETH.put(p.getCurrencyPairName(), new BigDecimal(p.getLastOrderRate())));

            final List<WalletTotalUsdDto> walletTotalUsdDtoList = new ArrayList<>();
            for (MyWalletsStatisticsDto myWalletsStatisticsDto : resultWallet) {
                WalletTotalUsdDto walletTotalUsdDto = new WalletTotalUsdDto(myWalletsStatisticsDto.getCurrencyName());
                Map<String, BigDecimal> mapWalletTotalUsdDto = new HashMap<>();
                if (myWalletsStatisticsDto.getCurrencyName().equals("USD")) {
                    walletTotalUsdDto.setSumUSD(new BigDecimal(myWalletsStatisticsDto.getTotalBalance()));
                    walletTotalUsdDto.setRates(mapWalletTotalUsdDto);
                    walletTotalUsdDtoList.add(walletTotalUsdDto);
                }
                resultOrders.stream()
                        .filter(o -> o.getCurrencyPairName().equals(myWalletsStatisticsDto.getCurrencyName().concat("/USD"))
                                || o.getCurrencyPairName().equals(myWalletsStatisticsDto.getCurrencyName().concat("/BTC"))
                                || o.getCurrencyPairName().equals(myWalletsStatisticsDto.getCurrencyName().concat("/ETH"))
                        )
                        .forEach(o -> {
                            mapWalletTotalUsdDto.put(o.getCurrencyPairName(), new BigDecimal(o.getLastOrderRate()));
                        });
                if (!mapWalletTotalUsdDto.isEmpty()) {
                    walletTotalUsdDto.setTotalBalance(new BigDecimal(myWalletsStatisticsDto.getTotalBalance()));
                    walletTotalUsdDto.setRates(mapWalletTotalUsdDto);
                    walletTotalUsdDtoList.add(walletTotalUsdDto);
                }
            }

            walletTotalUsdDtoList.stream().forEach(wallet -> {
                if (wallet.getRates().containsKey(wallet.getCurrency().concat("/USD"))) {
                    wallet.setSumUSD(wallet.getRates().get(wallet.getCurrency().concat("/USD")).multiply(wallet.getTotalBalance()));
                } else if (wallet.getRates().containsKey(wallet.getCurrency().concat("/BTC"))) {
                    wallet.setSumUSD(wallet.getRates().get(wallet.getCurrency().concat("/BTC"))
                            .multiply(wallet.getTotalBalance()).multiply(ratesBTC_ETH.get("BTC/USD")));
                } else if (wallet.getRates().containsKey(wallet.getCurrency().concat("/ETH"))) {
                    wallet.setSumUSD(wallet.getRates().get(wallet.getCurrency().concat("/ETH"))
                            .multiply(wallet.getTotalBalance()).multiply(ratesBTC_ETH.get("ETH/USD")));
                }
            });

            map.put("sumTotalUSD", walletTotalUsdDtoList.stream().mapToDouble(w -> w.getSumUSD().doubleValue()).sum());
        }

        return map;

    }


    @RequestMapping(value = "/dashboard/currencyPairStatistic", method = RequestMethod.GET)
    public Map<String, ?> getCurrencyPairStatisticsForAllCurrencies(
            @RequestParam(required = false) Boolean refreshIfNeeded,
            HttpServletRequest request, Principal principal) throws IOException {
        try {
            HttpSession session = request.getSession(true);
            String cacheKey = "currencyPairStatistic" + request.getHeader("windowid");
            refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
            CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
            return new HashMap<String, List<ExOrderStatisticsShortByPairsDto>>() {{
                put("list", orderService.getOrdersStatisticByPairs(cacheData, localeResolver.resolveLocale(request)));
            }};
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            throw e;
        } finally {
        }
    }

    @RequestMapping(value = {"/dashboard/firstentry"})
    public void setFirstEntryFlag(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("firstEntry", true);
        LOGGER.debug(" SESSION: " + session.getId() + " firstEntry: " + session.getAttribute("firstEntry"));

    }

    @RequestMapping(value = "/dashboard/chartArray/{type}", method = RequestMethod.GET)
    public ArrayList chartArray(HttpServletRequest request) {
        CurrencyPair currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
        if (currencyPair == null) {
            return new ArrayList();
        }
        final BackDealInterval backDealInterval = (BackDealInterval) request.getSession().getAttribute("currentBackDealInterval");
        ChartType chartType = (ChartType) request.getSession().getAttribute("chartType");
        log.error("chartType {}", chartType);
        /**/
        ArrayList<List> arrayListMain = new ArrayList<>();
        /*in first row return backDealInterval - to synchronize period menu with it*/
        arrayListMain.add(new ArrayList<Object>() {{
            add(backDealInterval);
        }});
        /**/
        if (chartType == ChartType.AREA) {
            /*GOOGLE*/
            List<Map<String, Object>> rows = orderService.getDataForAreaChart(currencyPair, backDealInterval);
            for (Map<String, Object> row : rows) {
                Timestamp dateAcception = (Timestamp) row.get("dateAcception");
                BigDecimal exrate = (BigDecimal) row.get("exrate");
                BigDecimal volume = (BigDecimal) row.get("volume");
                if (dateAcception != null) {
                    ArrayList<Object> arrayList = new ArrayList<>();
                    /*values*/
                    arrayList.add(dateAcception.toString());
                    arrayList.add(exrate.doubleValue());
                    arrayList.add(volume.doubleValue());
                    /*titles of values for chart tip*/
                    arrayList.add(messageSource.getMessage("orders.date", null, localeResolver.resolveLocale(request)));
                    arrayList.add(messageSource.getMessage("orders.exrate", null, localeResolver.resolveLocale(request)));
                    arrayList.add(messageSource.getMessage("orders.volume", null, localeResolver.resolveLocale(request)));
                    arrayListMain.add(arrayList);
                }
            }
        } else if (chartType == ChartType.CANDLE) {
            /*GOOGLE*/
            List<CandleChartItemDto> rows = orderService.getDataForCandleChart(currencyPair, backDealInterval);
            for (CandleChartItemDto candle : rows) {
                ArrayList<Object> arrayList = new ArrayList<>();
                /*values*/
                arrayList.add(candle.getBeginPeriod().toString());
                arrayList.add(candle.getEndPeriod().toString());
                arrayList.add(candle.getOpenRate());
                arrayList.add(candle.getCloseRate());
                arrayList.add(candle.getLowRate());
                arrayList.add(candle.getHighRate());
                arrayList.add(candle.getBaseVolume());
                arrayListMain.add(arrayList);
            }
        } else if (chartType == ChartType.STOCK) {
            /*AMCHARTS*/
            List<CandleChartItemDto> rows = orderService.getDataForCandleChart(currencyPair, backDealInterval);
            for (CandleChartItemDto candle : rows) {
                ArrayList<Object> arrayList = new ArrayList<>();
                /*values*/
                arrayList.add(candle.getBeginDate().toString());
                arrayList.add(candle.getEndDate().toString());
                arrayList.add(candle.getOpenRate());
                arrayList.add(candle.getCloseRate());
                arrayList.add(candle.getLowRate());
                arrayList.add(candle.getHighRate());
                arrayList.add(candle.getBaseVolume());
                arrayListMain.add(arrayList);
            }
        }
        request.getSession().setAttribute("currentBackDealInterval", backDealInterval);
        return arrayListMain;
    }

    @RequestMapping(value = "/dashboard/currentParams", method = RequestMethod.GET)
    public CurrentParams setCurrentParams(
            @RequestParam(required = false) String currencyPairName,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String chart,
            @RequestParam(required = false) Boolean showAllPairs,
            @RequestParam(required = false) Boolean orderRoleFilterEnabled,
            @RequestParam(defaultValue = "ALL") CurrencyPairType currencyPairType,
            HttpServletRequest request) {
        CurrencyPair currencyPair = getPairFormSessionOrRequest(request, currencyPairName, currencyPairType);
        currencyPair = resolveCurrentOrDefaultPairForType(currencyPair, currencyPairType);
        request.getSession().setAttribute("currentCurrencyPair", currencyPair);
        /**/
        if (showAllPairs == null) {
            if (request.getSession().getAttribute("showAllPairs") == null) {
                showAllPairs = false;
            } else {
                showAllPairs = (Boolean) request.getSession().getAttribute("showAllPairs");
            }
        }
        request.getSession().setAttribute("showAllPairs", showAllPairs);
        /**/
        BackDealInterval backDealInterval;
        if (period == null) {
            backDealInterval = (BackDealInterval) request.getSession().getAttribute("currentBackDealInterval");
            if (backDealInterval == null) {
                backDealInterval = BACK_DEAL_INTERVAL_DEFAULT;
            }
        } else {
            backDealInterval = new BackDealInterval(period);
        }
        request.getSession().setAttribute("currentBackDealInterval", backDealInterval);
        /**/
        ChartType chartType;
        if (chart == null) {
            chartType = (ChartType) request.getSession().getAttribute("chartType");
            if (chartType == null) {
                chartType = CHART_TYPE_DEFAULT;
            }
        } else {
            chartType = ChartType.convert(chart);
        }
        request.getSession().setAttribute("chartType", chartType);
        /**/
        if (orderRoleFilterEnabled == null) {
            if (request.getSession().getAttribute("orderRoleFilterEnabled") == null) {
                orderRoleFilterEnabled = false;
            } else {
                orderRoleFilterEnabled = (Boolean) request.getSession().getAttribute("orderRoleFilterEnabled");
            }
        }
        request.getSession().setAttribute("orderRoleFilterEnabled", orderRoleFilterEnabled);

        CurrentParams currentParams = new CurrentParams();
        currentParams.setCurrencyPair((CurrencyPair) request.getSession().getAttribute("currentCurrencyPair"));
        currentParams.setPeriod(((BackDealInterval) request.getSession().getAttribute("currentBackDealInterval")).getInterval());
        currentParams.setChartType(((ChartType) request.getSession().getAttribute("chartType")).getTypeName());
        currentParams.setShowAllPairs(((Boolean) request.getSession().getAttribute("showAllPairs")));
        currentParams.setOrderRoleFilterEnabled(((Boolean) request.getSession().getAttribute("orderRoleFilterEnabled")));
        return currentParams;
    }

    private CurrencyPair getPairFormSessionOrRequest(HttpServletRequest request, String currencyPairName, CurrencyPairType type) {
        CurrencyPair currencyPair = null;
        if (StringUtils.isEmpty(currencyPairName)) {
            if (request.getSession().getAttribute("currentCurrencyPair") != null) {
                currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
            }
        } else {
            List<CurrencyPair> currencyPairs = currencyService.getAllCurrencyPairs(type);
            if (!currencyPairs.isEmpty()) {
                currencyPair = currencyPairs
                        .stream()
                        .filter(e -> e.getName().equalsIgnoreCase(currencyPairName))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Unsupported pair"));
            }
        }
        return currencyPair;
    }

    private CurrencyPair resolveCurrentOrDefaultPairForType(CurrencyPair currencyPair, CurrencyPairType type) {
        if (currencyPair != null && type != CurrencyPairType.ALL && currencyPair.getPairType() != type) {
            currencyPair = null;
        }
        if (currencyPair == null) {
            switch (type) {
                case MAIN: {
                }
                case ALL: {
                    currencyPair = currencyService.getCurrencyPairByName("BTC/USD");
                    break;
                }
                case ICO: {
                    List<CurrencyPair> currencyPairs = currencyService.getAllCurrencyPairs(type);
                    if (currencyPairs.isEmpty()) {
                        throw new RuntimeException("no pairs for thios type");
                    } else {
                        currencyPair = currencyPairs.get(0);
                    }
                    break;
                }
            }
        }
        return currencyPair;
    }


    @RequestMapping(value = "/dashboard/tableParams/{tableId}", method = RequestMethod.GET)
    public TableParams setTableParams(
            @PathVariable String tableId,
            @RequestParam(required = false) Integer limitValue,
            @RequestParam(required = false) OrderStatus orderStatusValue,
            HttpServletRequest request) {
        /**/
        String attributeName = tableId + "Params";
        TableParams tableParams = (TableParams) request.getSession().getAttribute(attributeName);
        if (tableParams == null) {
            tableParams = new TableParams();
            tableParams.setTableId(tableId);
        }
        /**/
        Integer limit;
        if (limitValue == null) {
            limit = tableParams.getPageSize();
            if (limit == null) {
                limit = TABLES_LIMIT_DEFAULT;
            }
        } else {
            limit = limitValue;
        }
        tableParams.setPageSize(limit);
        /**/
        request.getSession().setAttribute(attributeName, tableParams);
        return tableParams;
    }

    @RequestMapping(value = "/dashboard/ordersForPairStatistics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ExOrderStatisticsDto getNewCurrencyPairData(HttpServletRequest request) {
        CurrencyPair currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
        if (currencyPair == null) {
            return null;
        }
        BackDealInterval backDealInterval = (BackDealInterval) request.getSession().getAttribute("currentBackDealInterval");
        /**/
        ExOrderStatisticsDto exOrderStatisticsDto = orderService.getOrderStatistic(currencyPair, backDealInterval, localeResolver.resolveLocale(request));
        return exOrderStatisticsDto;
    }

    @RequestMapping(value = "/dashboard/createPairSelectorMenu", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, List<CurrencyPair>> getCurrencyPairNameList(@RequestParam(value = "pairs", defaultValue = "MAIN") String pairType, HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);
        CurrencyPairType cpType = Preconditions.checkNotNull(CurrencyPairType.valueOf(pairType));
        List<CurrencyPair> list = currencyService.getAllCurrencyPairsInAlphabeticOrder(cpType);
        if (cpType == CurrencyPairType.ALL) {
            list.forEach(p -> {
                if (p.getPairType() == CurrencyPairType.ICO) {
                    p.setMarket(CurrencyPairType.ICO.name());
                }
            });
        }
        list.forEach(p -> p.setMarketName(messageSource.getMessage("message.cp.".concat(p.getMarket()), null, locale)));
        return list.stream().sorted(Comparator.comparing(CurrencyPair::getName)).collect(Collectors.groupingBy(CurrencyPair::getMarket));
    }


    @RequestMapping(value = "/dashboard/acceptedOrderHistory/{scope}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderAcceptedHistoryDto> getOrderHistory(@RequestParam(required = false) Boolean refreshIfNeeded,
                                                         @PathVariable String scope,
                                                         Principal principal,
                                                         HttpServletRequest request) {
        String email = principal == null || "ALL".equals(scope.toUpperCase()) ? "" : principal.getName();
        CurrencyPair currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
        if (currencyPair == null) {
            return Collections.EMPTY_LIST;
        }
        String cacheKey = "acceptedOrderHistory" + email + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        List<OrderAcceptedHistoryDto> result = orderService.getOrderAcceptedForPeriod(cacheData, email, ORDER_HISTORY_INTERVAL, ORDER_HISTORY_LIMIT, currencyPair, localeResolver.resolveLocale(request));
        return result;
    }

    @RequestMapping(value = "/dashboard/orderCommissions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderCommissionsDto getOrderCommissions() {
        OrderCommissionsDto result = orderService.getCommissionForOrder();
        return result;
    }

    @RequestMapping(value = "/dashboard/sellOrders", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderListDto> getSellOrdersList(@RequestParam(required = false) Boolean refreshIfNeeded,
                                                Principal principal, HttpServletRequest request) {
        CurrencyPair currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
        if (currencyPair == null) {
            return Collections.EMPTY_LIST;
        }
        Boolean orderRoleFilterEnabled = (Boolean) request.getSession().getAttribute("orderRoleFilterEnabled");
        if (orderRoleFilterEnabled == null) {
            orderRoleFilterEnabled = false;
        }
        String cacheKey = "sellOrders" + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        List<OrderListDto> result = orderService.getAllSellOrders(cacheData, currencyPair, localeResolver.resolveLocale(request), orderRoleFilterEnabled);
        return result;
    }

    @RequestMapping(value = "/dashboard/BuyOrders", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderListDto> getBuyOrdersList(@RequestParam(required = false) Boolean refreshIfNeeded,
                                               Principal principal, HttpServletRequest request) {
        CurrencyPair currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
        if (currencyPair == null) {
            return Collections.EMPTY_LIST;
        }
        Boolean orderRoleFilterEnabled = (Boolean) request.getSession().getAttribute("orderRoleFilterEnabled");

        if (orderRoleFilterEnabled == null) {
            orderRoleFilterEnabled = false;
        }
        String cacheKey = "BuyOrders" + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        List<OrderListDto> result = orderService.getAllBuyOrders(cacheData, currencyPair, localeResolver.resolveLocale(request), orderRoleFilterEnabled);
        return result;
    }

    @RequestMapping(value = "/dashboard/myWalletsData", method = RequestMethod.GET)
    public List<MyWalletsDetailedDto> getMyWalletsData(@RequestParam(required = false) Boolean refreshIfNeeded,
                                                       Principal principal, HttpServletRequest request) {
        if (principal == null) {
            return null;
        }
        String email = principal.getName();
        String cacheKey = "myWalletsData" + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        List<MyWalletsDetailedDto> result = walletService.getAllWalletsForUserDetailed(cacheData, email, Locale.ENGLISH);
        return result;
    }

    @RequestMapping(value = "/dashboard/myOrdersData/{tableId}", method = RequestMethod.GET)
    public List<OrderWideListDto> getMyOrdersData(
            @RequestParam(required = false) Boolean refreshIfNeeded,
            @PathVariable("tableId") String tableId,
            @RequestParam(required = false) OperationType type,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String scope,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) PagingDirection direction,
            @RequestParam(value = "baseType", defaultValue = "LIMIT") OrderBaseType orderBaseType,
            Principal principal,
            HttpServletRequest request) {
        if (principal == null) {
            return null;
        }
        LOGGER.debug("Scope: " + scope);
        String email = principal.getName();
        CurrencyPair currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
        if (currencyPair == null) {
            return Collections.EMPTY_LIST;
        }
        Boolean showAllPairs = (Boolean) request.getSession().getAttribute("showAllPairs");
        /**/
        String attributeName = tableId + "Params";
        TableParams tableParams = (TableParams) request.getSession().getAttribute(attributeName);
        requireNonEmpty(tableParams, "The parameters are not populated for the " + tableId);
        tableParams.setOffsetAndLimitForSql(page, direction);
        /**/
        String cacheKey = "myOrdersData" + tableId + status + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        List<OrderWideListDto> result;
        switch (orderBaseType) {
            case STOP_LIMIT: {
                result = stopOrderService.getMyOrdersWithState(cacheData, email,
                        showAllPairs == null || !showAllPairs ? currencyPair : null,
                        status, type, scope, tableParams.getOffset(), tableParams.getLimit(), localeResolver.resolveLocale(request));
                break;
            }
            default: {
                result = orderService.getMyOrdersWithState(cacheData, email,
                        showAllPairs == null || !showAllPairs ? currencyPair : null,
                        status, type, scope, tableParams.getOffset(), tableParams.getLimit(), localeResolver.resolveLocale(request));
            }
        }
        if (!result.isEmpty()) {
            result.get(0).setPage(tableParams.getPageNumber());
        }
        tableParams.updateEofState(result);
        return result;
    }

    @RequestMapping(value = "/dashboard/myOrdersData", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Future<List<OrderWideListDto>> getMyOrders(@RequestParam("tableType") String tableType,
                                                      @RequestParam(required = false) String scope,
                                                      Principal principal, HttpServletRequest request) {

        CurrencyPair currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
        Boolean showAllPairs = (Boolean) request.getSession().getAttribute("showAllPairs");
        String email = principal != null ? principal.getName() : "";
        return CompletableFuture.supplyAsync(() -> getOrderWideListDtos(tableType, showAllPairs == null || !showAllPairs ? currencyPair : null, scope, email, localeResolver.resolveLocale(request)));
    }

    private List<OrderWideListDto> getOrderWideListDtos(String tableType, CurrencyPair currencyPair, String scope, String email, Locale locale) {
        List<OrderWideListDto> result = new ArrayList<>();
        switch (tableType) {
            case "CLOSED":
                List<OrderWideListDto> ordersSellClosed = orderService.getMyOrdersWithState(email, currencyPair, OrderStatus.CLOSED, null, scope, 0, -1, locale);
                result = ordersSellClosed;
                break;
            case "CANCELLED":
                List<OrderWideListDto> ordersSellCancelled = orderService.getMyOrdersWithState(email, currencyPair, OrderStatus.CANCELLED, null, scope, 0, -1, locale);
                result = ordersSellCancelled;
                break;
            case "OPENED":
                List<OrderWideListDto> ordersSellOpened = orderService.getMyOrdersWithState(email, currencyPair, OrderStatus.OPENED, null, scope, 0, -1, locale);
                result = ordersSellOpened;
                break;
        }
        return result;
    }

    @RequestMapping(value = "/dashboard/myReferralData/{tableId}", method = RequestMethod.GET)
    public List<MyReferralDetailedDto> getMyReferralData(
            @RequestParam(required = false) Boolean refreshIfNeeded,
            @PathVariable("tableId") String tableId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) PagingDirection direction,
            Principal principal,
            HttpServletRequest request) {
        if (principal == null) {
            return null;
        }
        String email = principal.getName();
        /**/
        String attributeName = tableId + "Params";
        TableParams tableParams = (TableParams) request.getSession().getAttribute(attributeName);
        requireNonEmpty(tableParams, "The parameters are not populated for the " + tableId);
        tableParams.setOffsetAndLimitForSql(page, direction);
        /**/
        String cacheKey = "myReferralData" + tableId + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        List<MyReferralDetailedDto> result = referralService.findAllMyReferral(cacheData, email, tableParams.getOffset(), tableParams.getLimit(), localeResolver.resolveLocale(request));
        if (!result.isEmpty()) {
            result.get(0).setPage(tableParams.getPageNumber());
        }
        Locale locale = localeResolver.resolveLocale(request);

        tableParams.updateEofState(result);
        return result;
    }

    @RequestMapping(value = "/dashboard/myStatementData/{tableId}/{walletId}", method = RequestMethod.GET)
    public List<AccountStatementDto> getMyAccountStatementData(
            @RequestParam(required = false) Boolean refreshIfNeeded,
            @PathVariable("tableId") String tableId,
            @PathVariable("walletId") String walletId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) PagingDirection direction,
            HttpServletRequest request) {
        /**/
        String attributeName = tableId + "Params";
        TableParams tableParams = (TableParams) request.getSession().getAttribute(attributeName);
        requireNonEmpty(tableParams, "The parameters are not populated for the " + tableId);
        tableParams.setOffsetAndLimitForSql(page, direction);
        /**/
        String cacheKey = "myAccountStatement" + tableId + walletId + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        List<AccountStatementDto> result = transactionService.getAccountStatement(cacheData, Integer.valueOf(walletId), tableParams.getOffset(), tableParams.getLimit(), localeResolver.resolveLocale(request));
        if (!result.isEmpty()) {
            result.get(0).setPage(tableParams.getPageNumber());
        }
        tableParams.updateEofState(result);
        return result;
    }
    //TODO should be done
    @RequestMapping(value = "/dashboard/myInputoutputData/{tableId}", method = RequestMethod.GET)
    public List<MyInputOutputHistoryDto> getMyInputoutputData(
            @RequestParam(required = false) Boolean refreshIfNeeded,
            @PathVariable("tableId") String tableId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) PagingDirection direction,
            Principal principal,
            HttpServletRequest request) {
        if (principal == null) {
            return null;
        }
        String email = principal.getName();
        /**/
        String attributeName = tableId + "Params";
        TableParams tableParams = (TableParams) request.getSession().getAttribute(attributeName);
        requireNonEmpty(tableParams, "The parameters are not populated for the " + tableId);
        tableParams.setOffsetAndLimitForSql(page, direction);
        /**/
        String cacheKey = "myInputoutputData" + tableId + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        List<MyInputOutputHistoryDto> result = inputOutputService.getMyInputOutputHistory(cacheData, email, tableParams.getOffset(), tableParams.getLimit(), localeResolver.resolveLocale(request));
        if (!result.isEmpty()) {
            result.get(0).setPage(tableParams.getPageNumber());
        }
        tableParams.updateEofState(result);
        return result;
    }

    @RequestMapping(value = "/dashboard/news/{tableId}", method = RequestMethod.GET)
    public List<NewsDto> getNewsList(
            @PathVariable("tableId") String tableId,
            @RequestParam(required = false) Boolean refreshIfNeeded,
            @RequestParam(required = false) Integer page,
            HttpServletRequest request) {
        String attributeName = tableId + "Params";
        TableParams tableParams = (TableParams) request.getSession().getAttribute(attributeName);
        requireNonEmpty(tableParams, "The parameters are not populated for the " + tableId);
        Integer offset = page == null || tableParams.getPageSize() == -1 ? 0 : (page - 1) * tableParams.getPageSize();
        String cacheKey = "newsList" + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        List<NewsDto> result = newsService.getNewsBriefList(cacheData, offset, tableParams.getPageSize(), localeResolver.resolveLocale(request));
        return result;
    }

    @RequestMapping(value = "/dashboard/newsTwitter", method = RequestMethod.GET)
    public List<NewsDto> getTwitterNewsList(@RequestParam(value = "amount", defaultValue = "50") int amount) {
        return newsService.getTwitterNews(amount);
    }

    @RequestMapping(value = "/dashboard/myReferralStructure")
    public RefsListContainer getMyReferralData(
            @RequestParam("action") String action,
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(value = "onPage", defaultValue = "20") int onPage,
            @RequestParam(value = "page", defaultValue = "1") int page,
            RefFilterData refFilterData,
            Principal principal) {
        if (principal == null) {
            return null;
        }
        String email = principal.getName();
        /**/
        return referralService.getRefsContainerForReq(action, userId, userService.getIdByEmail(email), onPage, page, refFilterData);
    }

    @ResponseBody
    @RequestMapping(value = "/dashboard/getAllCurrencies")
    public List getAllCurrencies() {
        return currencyService.findAllCurrenciesWithHidden();
    }

}
