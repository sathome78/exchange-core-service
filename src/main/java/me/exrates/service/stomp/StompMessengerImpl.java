package me.exrates.service.stomp;

import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.exrates.cache.ChartsCache;
import me.exrates.model.chart.ChartTimeFrame;
import me.exrates.model.dto.ChartPeriodsEnum;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.RefreshObjectsEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.main.BackDealInterval;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2(topic = "ws_stomp_log")
@Component
public class StompMessengerImpl implements StompMessenger {

    @Autowired
    private OrderService orderService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private DefaultSimpUserRegistry registry;
    @Autowired
    private UserService userService;
    @Autowired
    private ChartsCache chartsCache;

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void init() {
        scheduler.scheduleAtFixedRate(() ->  registry.findSubscriptions(sub -> true)
                        .forEach(sub -> System.out.printf("sub: dest %s, user %s")),
                1, 2, TimeUnit.MINUTES);
    }


    private final List<BackDealInterval> intervals = Arrays.stream(ChartPeriodsEnum.values())
                                                    .map(ChartPeriodsEnum::getBackDealInterval)
                                                    .collect(Collectors.toList());


   @Override
   public void sendRefreshTradeOrdersMessage(Integer pairId, OperationType operationType){
       String message = orderService.getOrdersForRefresh(pairId, operationType, null);
       sendMessageToDestination("/app/trade_orders/".concat(pairId.toString()), message);
       sendRefreshTradeOrdersMessageToFiltered(pairId, operationType);
   }

   private void sendRefreshTradeOrdersMessageToFiltered(Integer pairId, OperationType operationType) {
      Set<SimpSubscription> subscriptions =
              findSubscribersByDestination("/user/queue/trade_orders/f/".concat(pairId.toString()));
      if (!subscriptions.isEmpty()) {
          Map<UserRole, List<SimpSubscription>> map = new HashMap<>();
          subscriptions.forEach(p -> {
              String userEmail = p.getSession().getUser().getName();
              if (!StringUtils.isEmpty(userEmail)) {
                  UserRole role = userService.getUserRoleFromDB(userEmail);
                  if (map.containsKey(role)) {
                      map.get(role).add(p);
                  } else {
                      map.put(role, new ArrayList<SimpSubscription>(){{add(p);}});
                  }
              }
          });
          map.forEach((k,v) -> {
              String message = orderService.getOrdersForRefresh(pairId, operationType, k);
              for (SimpSubscription subscription : v) {
                  sendMessageToSubscription(subscription, message, "/queue/trade_orders/f/".concat(pairId.toString()));
              }
          });
      }
   }

   @Override
   public void sendMyTradesToUser(final int userId, final Integer currencyPair) {
       String userEmail = userService.getEmailById(userId);
       String destination = "/queue/personal/".concat(currencyPair.toString());
       String message = orderService.getTradesForRefresh(currencyPair, userEmail, RefreshObjectsEnum.MY_TRADES);
       messagingTemplate.convertAndSendToUser(userEmail, destination, message);
   }

    @Override
    public void sendAllTrades(final Integer currencyPair) {
        String destination = "/app/trades/".concat(currencyPair.toString());
        String message = orderService.getTradesForRefresh(currencyPair, null, RefreshObjectsEnum.ALL_TRADES);
        sendMessageToDestination(destination, message);
    }

    @Override
    public void sendChartData(final Integer currencyPairId) {
       Map<String, String> data = chartsCache.getData(currencyPairId);
        orderService.getIntervals().forEach(p-> {
            String message = data.get(p.getInterval());
            String destination = "/app/charts/".concat(currencyPairId.toString().concat("/").concat(p.getInterval()));
            sendMessageToDestination(destination, message);
        });
    }



    @Override
    public void sendChartData(final Integer currencyPairId, String resolution, String data) {
        log.error("send chart data to {} {}", currencyPairId, resolution);
        String destination = "/app/charts/".concat(currencyPairId.toString().concat("/").concat(resolution));
        sendMessageToDestination(destination, data);
    }


    @Synchronized
    @Override
    public void sendStatisticMessage(List<Integer> currenciesIds) {
        Map<RefreshObjectsEnum, String> result =  orderService.getSomeCurrencyStatForRefresh(currenciesIds);
        result.forEach((k,v) -> {
            sendMessageToDestination("/app/statistics/".concat(k.getSubscribeChannel()), v);
        });
    }

    private Set<SimpSubscription> findSubscribersByDestination(final String destination) {
       return registry.findSubscriptions(subscription -> subscription.getDestination().equals(destination));
   }

   private void sendMessageToDestination(String destination, String message) {
       messagingTemplate.convertAndSend(destination, message);
   }

   private void sendMessageToSubscription(SimpSubscription subscription, String message, String dest) {
       sendMessageToDestinationAndUser(subscription.getSession().getUser().getName(), dest, message);
   }

    private void sendMessageToDestinationAndUser(final String user, String destination, String message) {
       messagingTemplate.convertAndSendToUser(user,
                                               destination,
                                               message);
    }

}
