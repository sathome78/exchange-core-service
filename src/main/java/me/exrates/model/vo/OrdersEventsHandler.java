package me.exrates.model.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.enums.OperationType;
import me.exrates.service.stomp.StompMessenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Maks on 28.08.2017.
 */
@Log4j2
@EqualsAndHashCode
@Getter
@Setter
public class OrdersEventsHandler {

    @Autowired
    private StompMessenger stompMessenger;

    private Integer pairId;
    private OperationType operationType;

    private AtomicInteger eventsCount = new AtomicInteger(0);

    private final Semaphore SEMAPHORE = new Semaphore(1, true);

    private volatile float loadFactor = 1;
    private final long refreshTime = 900; /*in millis*/
    private static final long MIN_REFRESH_TIME = 600;
    private static final long MAX_REFRESH_TIME = 1000;

    private Timer timer;

    private static final int MAX_EVENTS = 3;
    private static final int MIN_EVENTS = 1;
    private int lastEventsCountBeforeSend;


    private OrdersEventsHandler(Integer pairId, OperationType operationType) {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        this.pairId = pairId;
        this.operationType = operationType;
        timer = new Timer();
    }

    public static OrdersEventsHandler init(Integer pairId, OperationType operationType) {
        return new OrdersEventsHandler(pairId, operationType);
    }

    public void onOrderEvent() {
        eventsCount.incrementAndGet();
        calculateLoadFactor();
        if (SEMAPHORE.tryAcquire()) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    lastEventsCountBeforeSend = eventsCount.get();
                    eventsCount.set(0);
                    SEMAPHORE.release();
                    stompMessenger.sendRefreshTradeOrdersMessage(pairId, operationType);
                }
            }, getProperlyRefreshTime((long) (loadFactor * refreshTime)));
        }
    }


    private void calculateLoadFactor() {
        float diff = lastEventsCountBeforeSend / MAX_EVENTS;
        setLoadFactor(getLoadFactor() / diff);
    }

    private long getProperlyRefreshTime(long calculatedRefreshTime) {
        return Long.max(Long.min(calculatedRefreshTime, MAX_REFRESH_TIME), MIN_REFRESH_TIME);
    }


}
