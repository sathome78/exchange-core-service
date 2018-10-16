package me.exrates.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.chart.ChartTimeFrame;
import me.exrates.model.dto.CandleChartItemDto;
import me.exrates.model.dto.CandleDto;
import me.exrates.model.dto.ChartCacheUpdateEvent;
import me.exrates.service.OrderService;
import me.exrates.service.stomp.StompMessenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Log4j2(topic = "cache")
@Component
public class ChartsCacheManager {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private StompMessenger stompMessenger;
    @Autowired
    private ObjectMapper objectMapper;


    private Map<Integer, Map<String, ChartCacheUnit>> cacheMap = new ConcurrentHashMap<>();

    @Async
    public void onUpdateEvent(int pairId) {
        List<ChartTimeFrame> allIntervals = orderService.getChartTimeFrames();
        allIntervals.forEach(p -> setNeedUpdate(pairId, p));
    }


    private void setNeedUpdate(Integer pairId, ChartTimeFrame timeFrame) {
        ChartsCacheInterface cacheUnit = getRequiredCache(pairId, timeFrame);
        cacheUnit.setNeedToUpdate();
    }

    public List<CandleChartItemDto> getData(Integer pairId, ChartTimeFrame timeFrame, boolean lastOnly) {
        ChartsCacheInterface cacheUnit = getRequiredCache(pairId, timeFrame);
        return lastOnly ? cacheUnit.getLastData() : cacheUnit.getData();
    }

    public String getPreparedData(int pairId, ChartTimeFrame timeFrame, boolean lastOnly) {
        return prepareDataToSend(getData(pairId, timeFrame, lastOnly), pairId, timeFrame);
    }

    private ChartsCacheInterface getRequiredCache(Integer pairId, ChartTimeFrame timeFrame) {
        return cacheMap.computeIfAbsent(pairId, p -> {
            Map<String, ChartCacheUnit> map = new ConcurrentHashMap<>();
            orderService.getChartTimeFrames().forEach(i -> {
                map.put(i.getResolution().toString(), new ChartCacheUnit(pairId,
                        i,
                        orderService,
                        eventPublisher)
                );
            });
            return map;
        }).get(timeFrame.getResolution().toString());
    }


    @Async
    @EventListener
    void handleChartUpdate(ChartCacheUpdateEvent event) {
        List<CandleChartItemDto> data = (List<CandleChartItemDto>) event.getSource();
        String dataToSend = prepareDataToSend(data, event.getPairId(), event.getTimeFrame());
        stompMessenger.sendChartData(event.getPairId(),
                event.getTimeFrame().getResolution().toString(),
                dataToSend);
    }

    private String prepareDataToSend(List<CandleChartItemDto> data, Integer currencyPairId, final ChartTimeFrame backDealInterval) {
        List<CandleDto> resultData = data.stream().map(CandleDto::new).collect(Collectors.toList());
        try {
            return objectMapper.writeValueAsString(resultData);
        } catch (JsonProcessingException e) {
            log.error(e);
            return null;
        }

    }


}
