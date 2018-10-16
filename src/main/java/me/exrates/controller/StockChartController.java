package me.exrates.controller;

import me.exrates.model.dto.ChartTimeFrameDto;
import me.exrates.service.StockChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class StockChartController {

    @Autowired
    private StockChartService stockChartService;

    @ResponseBody
    @RequestMapping(value = "/stockChart/timeFrames", method = RequestMethod.GET)
    public Map<String, ChartTimeFrameDto> getTimeFrames() {
        return stockChartService.getTimeFramesByResolutions();
    }


}
