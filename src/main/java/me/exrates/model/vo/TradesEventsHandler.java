package me.exrates.model.vo;

import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;
import me.exrates.service.stomp.StompMessenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.concurrent.Semaphore;

/**
 * Created by Maks on 04.09.2017.
 */
@EqualsAndHashCode
@Log4j2
public class TradesEventsHandler {

    @Autowired
    private StompMessenger stompMessenger;

    private int currencyPairId;

    private final Semaphore SEMAPHORE = new Semaphore(1, true);

    private static final int LATENCY = 1200;


    private TradesEventsHandler(int currencyPairId) {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        this.currencyPairId = currencyPairId;
    }

    public static TradesEventsHandler init(int currencyPairId) {
        return new TradesEventsHandler(currencyPairId);
    }

    public void onAcceptOrderEvent() {
        if (SEMAPHORE.tryAcquire()) {
            try {
                Thread.sleep(LATENCY);
            } catch (InterruptedException e) {
                log.error("interrupted ", e);
            }
            SEMAPHORE.release();
            stompMessenger.sendAllTrades(currencyPairId);
        }

    }
}
