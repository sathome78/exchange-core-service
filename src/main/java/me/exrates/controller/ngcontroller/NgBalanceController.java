package me.exrates.controller.ngcontroller;

import lombok.extern.log4j.Log4j2;
import me.exrates.controller.ngcontroller.model.RefillPendingRequestDto;
import me.exrates.controller.ngcontroller.service.NgWalletService;
import me.exrates.controller.ngcontroller.service.RefillPendingRequestService;
import me.exrates.model.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.service.InputOutputService;
import me.exrates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping(value = "/info/private/v2/balances/",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Log4j2
public class NgBalanceController {

    private final UserService userService;

    private final RefillPendingRequestService refillPendingRequestService;

    private final InputOutputService inputOutputService;

    private final LocaleResolver localeResolver;

    private final NgWalletService ngWalletService;

    @Autowired
    public NgBalanceController(UserService userService, RefillPendingRequestService refillPendingRequestService, InputOutputService inputOutputService, LocaleResolver localeResolver, NgWalletService ngWalletService) {
        this.userService = userService;
        this.refillPendingRequestService = refillPendingRequestService;
        this.inputOutputService = inputOutputService;
        this.localeResolver = localeResolver;
        this.ngWalletService = ngWalletService;
    }

    @GetMapping
    public List<MyWalletsDetailedDto> getBalances() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ngWalletService.getAllWalletsForUserDetailed(email, Locale.ENGLISH);
    }

    @GetMapping("/getPendingRequests")
    public List<RefillPendingRequestDto> getPendingRequests() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return refillPendingRequestService.getPendingRefillRequests(userService.getIdByEmail(email));
    }

    //@OnlineMethod TODO check
    @RequestMapping(value = "/getInputOutputData/{tableId}", method = RequestMethod.GET)
    public List<MyInputOutputHistoryDto> getMyInputOutputData(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) String currency,
            @RequestParam String dateFrom,
            @RequestParam String dateTo,
            HttpServletRequest request) {
        log.info("Got params for getInputOutputData request " + limit + " " + offset + " " + dateFrom + " " + dateTo);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return inputOutputService.getMyInputOutputHistory(email, offset == null ? 0 : offset, limit == null ? 28 : limit, dateFrom, dateTo, localeResolver.resolveLocale(request), currency);
    }

}