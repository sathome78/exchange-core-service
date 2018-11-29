package me.exrates.controller.ngcontroller;

import me.exrates.controller.ngcontroller.service.NgOrderService;
import me.exrates.model.dto.CandleDto;
import me.exrates.model.dto.ChartTimeFramesEnum;
import me.exrates.model.main.CurrencyPair;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.QueryParam;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/info/public/v2/graph")
public class NgChartController {

    private final CurrencyService currencyService;
    private final NgOrderService ngOrderService;
    private final OrderService orderService;

    @Autowired
    public NgChartController(CurrencyService currencyService,
                             NgOrderService ngOrderService,
                             OrderService orderService) {
        this.currencyService = currencyService;
        this.ngOrderService = ngOrderService;
        this.orderService = orderService;
    }

    @GetMapping("/history")
    public ResponseEntity getCandleChartHistoryData(
            @QueryParam("symbol") String symbol,
            @QueryParam("to") Long to,
            @QueryParam("from") Long from,
            @QueryParam("resolution") String resolution,
            @QueryParam("countback") String countback) {

        CurrencyPair currencyPair = currencyService.getCurrencyPairByName(symbol);
        List<CandleDto> result = new ArrayList<>();
        if (currencyPair == null) {
            HashMap<String, Object> errors = new HashMap<>();
            errors.putAll(ngOrderService.filterDataPeriod(result, from, to, resolution));
            errors.put("s", "error");
            errors.put("errmsg", "can not find currencyPair");
            return new ResponseEntity(errors, HttpStatus.NOT_FOUND);
        }

        String rsolutionForChartTime = (resolution.equals("W") || resolution.equals("M")) ? "D" : resolution;
        result = orderService.getCachedDataForCandle(currencyPair,
                ChartTimeFramesEnum.ofResolution(rsolutionForChartTime).getTimeFrame())
                .stream().map(CandleDto::new).collect(Collectors.toList());
        return new ResponseEntity(ngOrderService.filterDataPeriod(result, from, to, resolution), HttpStatus.OK);
    }

    @GetMapping("/timescale_marks")
    public ResponseEntity getCandleTimeScaleMarks(
            @QueryParam("symbol") String symbol,
            @QueryParam("to") Long to,
            @QueryParam("from") Long from,
            @QueryParam("resolution") String resolution,
            @QueryParam("countback") String countback) {

        return getCandleChartHistoryData(symbol, to, from, resolution, countback);
    }

    @GetMapping(value = "/config")
    public ResponseEntity getChartConfig() {
        return new ResponseEntity(getConfig().toString(), HttpStatus.OK);
    }

    @GetMapping(value = "/symbols")
    public ResponseEntity getChartSymbol(@QueryParam("symbol") String symbol) {
        return new ResponseEntity(getSymbolInfo(symbol).toString(), HttpStatus.OK);
    }

    @GetMapping(value = "/time")
    public ResponseEntity getChartTime() {
        return new ResponseEntity(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC), HttpStatus.OK);
    }

    /**
     * Returns configurations for chart graphic
     *
     * @return
     */
    private JsonObject getConfig() {

        return Json.createObjectBuilder()
                .add("supports_search", true)
                .add("supports_group_request", false)
                .add("supports_marks", false)
                .add("supports_timescale_marks", true)
                .add("supports_time", true)
                .add("exchanges", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("value", "")
                                .add("name", "All Exchanges")
                                .add("desc", ""))
                        .add(Json.createObjectBuilder()
                                .add("value", "EXRATES")
                                .add("name", "EXRATES")
                                .add("desc", "EXRATES")))
                .add("symbols_types", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("name", "All types")
                                .add("value", "")))
                .add("supported_resolutions", Json.createArrayBuilder()
                        .add("30").add("60").add("240").add("720").add("D").add("2D").add("3D").add("W").add("3W").add("M"))
                .build();
    }

    private JsonObject getSymbolInfo(@QueryParam("symbol") String symbol) {

        return Json.createObjectBuilder()
                .add("name", symbol)
                .add("base_name", Json.createArrayBuilder().add(symbol))
                .add("description", "description")
                .add("full_name", symbol)
                .add("has_seconds", false)
                .add("has_intraday", true)
                .add("has_no_volume", false)
                .add("listed_exchange", "EXRATES")
                .add("exchange", "EXRATES")
                .add("minmov", 1)
                .add("fractional", false)
                .add("pricescale", 1000000000)
                .add("type", "bitcoin")
                .add("session", "24x7")
                .add("ticker", symbol)
                .add("timezone", "UTC")
                .add("supported_resolutions", Json.createArrayBuilder()
                        .add("30").add("60").add("240").add("720").add("D").add("2D").add("3D").add("W").add("3W").add("M"))
                .add("force_session_rebuild", false)
                .add("has_daily", true)
                .add("has_weekly_and_monthly", true)
                .add("has_empty_bars", true)
                .add("volume_precision", 2)
                .build();
    }

}
