package me.exrates.model.vo;

import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;
import me.exrates.service.stomp.StompMessenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

@EqualsAndHashCode
@Log4j2
public class MyTradesHandler {

    Map<Integer, Semaphore> locksMap = new ConcurrentHashMap<>();

    private Timer timer;

    @Autowired
    private StompMessenger stompMessenger;

    private int currencyPairId;

    private static final long LOCKS_CLEAR_DELAY = 1000 * 60 * 60 * 24;

    private static final int LATENCY = 1000;


    private MyTradesHandler(int currencyPairId) {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        this.currencyPairId = currencyPairId;
        timer = new Timer();
       /* timer.schedule(new TimerTask() {
            @Override
            public void run() {
                locksMap.clear();
            }
        }, LOCKS_CLEAR_DELAY);*/
    }

    public static MyTradesHandler init(int currencyPairId) {
        return new MyTradesHandler(currencyPairId);
    }

    public void onAcceptOrderEvent(int userId) {
        Semaphore semaphore = locksMap.computeIfAbsent(userId, k -> new Semaphore(1, true));
        log.debug("try to refresh {}", userId);
        if (semaphore.tryAcquire()) {
            try {
                log.debug("wait refresh {}", userId);
                Thread.sleep(LATENCY);
            } catch (InterruptedException e) {
                log.error("interrupted ", e);
            }
            semaphore.release();
            stompMessenger.sendMyTradesToUser(userId, currencyPairId);
        }
    }

}
