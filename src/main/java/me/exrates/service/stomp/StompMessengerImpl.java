package me.exrates.service.stomp;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2
@Component
public class StompMessengerImpl implements StompMessenger {

    @Autowired
    private SimpMessagingTemplate brokerMessagingTemplate;
    @Autowired
    private DefaultSimpUserRegistry registry;

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void init() {
        scheduler.scheduleAtFixedRate(() -> registry.findSubscriptions(sub -> true)
                        .forEach(sub -> System.out.printf("sub: dest %s, user %s", sub.getDestination(), sub.getId())),
                1, 2, TimeUnit.MINUTES);
    }

    @Override
    public void sendChartData(final Integer currencyPairId, String resolution, String data) {
        log.error("send chart data to {} {}", currencyPairId, resolution);
        String destination = "/app/charts/".concat(currencyPairId.toString().concat("/").concat(resolution));
        sendMessageToDestination(destination, data);
    }

    private void sendMessageToDestination(String destination, String message) {
        brokerMessagingTemplate.convertAndSend(destination, message);
    }

}
