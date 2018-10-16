package me.exrates.cache;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.chart.ChartTimeFrame;
import me.exrates.model.dto.CandleChartItemDto;
import me.exrates.model.dto.ChartCacheUpdateEvent;
import me.exrates.service.OrderService;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Maks on 25.01.2018.
 */
@Log4j2(topic = "cache")
@Data
public class ChartCacheUnit implements ChartsCacheInterface {

    private final int DEFAULT_LAST_ITEMS_NUMBER = 5;

    /*fields*/
    private List<CandleChartItemDto> cachedData;
    private final Integer currencyPairId;
    private final ChartTimeFrame timeFrame;
    private final long minUpdateIntervalSeconds;
    private AtomicBoolean needToUpdate = new AtomicBoolean(true);
    private LocalDateTime lastUpdateDate;
    private OrderService orderService;
    private ApplicationEventPublisher eventPublisher;
    /*provide update only when user try to get data, or update imediately, when time for update remaining*/
    private final boolean lazyUpdate;

    /*synchronizers*/

    private ReentrantLock lock = new ReentrantLock();

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private CyclicBarrier barrier = new CyclicBarrier(99999);

    private ReentrantLock timerLock = new ReentrantLock();
    private Condition myCondition = lock.newCondition();

    /*constructor*/
    public ChartCacheUnit(Integer currencyPairId,
                          ChartTimeFrame timeFrame,
                          OrderService orderService,
                          ApplicationEventPublisher eventPublisher) {
        this.currencyPairId = currencyPairId;
        this.timeFrame = timeFrame;
        this.minUpdateIntervalSeconds = timeFrame.getResolution().getTimeUnit().getRefreshDelaySeconds();
        this.lazyUpdate = /*timeFrame.getTimeUnit().isChartLazyUpdate();*/ true;
        this.eventPublisher = eventPublisher;
        this.orderService = orderService;
        cachedData = null;
    }


    /*methods*/

    @Override
    public List<CandleChartItemDto> getData() {
        if (cachedData == null || isUpdateCasheRequired()) {
            updateCache(cachedData != null);
        }
        return cachedData;
    }

    private boolean isUpdateCasheRequired() {
        return needToUpdate.get() && isTimeForUpdate() && lazyUpdate;
    }

    @Override
    public List<CandleChartItemDto> getLastData() {
        List<CandleChartItemDto> data = getData();
        if (data.size() <= DEFAULT_LAST_ITEMS_NUMBER) {
            return data;
        }
        return data.subList(data.size() - DEFAULT_LAST_ITEMS_NUMBER, data.size());
    }

    private boolean isTimeForUpdate() {
        return lastUpdateDate == null || lastUpdateDate.plusSeconds(minUpdateIntervalSeconds).compareTo(LocalDateTime.now()) <= 0;
    }

    //    @Override
//    public void setNeedToUpdate() {
//        log.debug("setting update data {} {}", currencyPairId, timeFrame);
//        if (!lazyUpdate) {
//            log.debug("not lazy update data {} {}", currencyPairId, timeFrame);
//            if (timerLock.tryLock()) {
//                timerLock.lock();
//                try {
//
//                    new Timer().schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            log.debug("execute task update data {} {}", currencyPairId, timeFrame);
//                            updateCache(true);
//                            eventPublisher.publishEvent(new ChartCacheUpdateEvent(getLastData(), timeFrame, currencyPairId));
//                        }
//                    }, getMinUpdateIntervalSeconds() * 1000);
//                } finally {
//                    timerLock.unlock();
//                }
//
//            }
//        } else {
//            needToUpdate.set(true);
//        }
//    }
    @Override
    public void setNeedToUpdate() {
        if (!lazyUpdate) {
            if (timerLock.tryLock()) {
                timerLock.lock();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        timerLock = new ReentrantLock();
                        updateCache(true);
                        eventPublisher.publishEvent(new ChartCacheUpdateEvent(getLastData(), timeFrame, currencyPairId));
                    }
                }, getMinUpdateIntervalSeconds() * 1000);

            }
        } else {
            needToUpdate.set(true);
        }
    }

    private LocalDateTime lastLock;


    private void updateCache(boolean appendLastEntriesOnly) {
        if (tryLockWithTimeout()) {
            lastLock = LocalDateTime.now();
            try {
                performUpdate(appendLastEntriesOnly);
                barrier.reset();
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else {
            try {
                barrier.await(30, TimeUnit.SECONDS);
                /*if (cachedData == null) {
                 *//*тут рекурсия получается, но без данных трэд не уйдет*//*
                        updateCache(appendLastEntriesOnly);
                    }*/
            } catch (Exception e) {
                log.warn(e);
            }
        }
    }

    private synchronized boolean tryLockWithTimeout() {
        if (lock.tryLock()) {
            return true;
        } else if (lastLock.plusSeconds(40).compareTo(LocalDateTime.now()) <= 0) {
            lock = new ReentrantLock();
            return lock.tryLock();
        } else return false;
    }

    private void performUpdate(boolean appendLastEntriesOnly) {
        if (appendLastEntriesOnly && cachedData != null && !cachedData.isEmpty()) {
            cachedData.forEach(System.out::println);
            CandleChartItemDto lastBar = cachedData.remove(cachedData.size() - 1);
            LocalDateTime lastBarStartTime = lastBar.getBeginPeriod();
            List<CandleChartItemDto> newData = orderService.getLastDataForCandleChart(currencyPairId, lastBarStartTime, timeFrame.getResolution());
            newData.forEach(System.out::println);
            cachedData.addAll(newData);
            cachedData.forEach(System.out::println);
        } else {
            setCachedData(orderService.getDataForCandleChart(currencyPairId, timeFrame));
        }
        lastUpdateDate = LocalDateTime.now();
        needToUpdate.set(false);
    }

}
