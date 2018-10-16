package me.exrates.controller;


import lombok.extern.log4j.Log4j2;
import me.exrates.exception.*;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderCreateSummaryDto;
import me.exrates.model.dto.OrderInfoDto;
import me.exrates.model.dto.OrderValidationDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.error.ErrorInfo;
import me.exrates.model.main.CurrencyPair;
import me.exrates.model.main.ExOrder;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import me.exrates.service.*;
import me.exrates.service.userOperation.UserOperationService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static me.exrates.model.enums.OrderActionEnum.CREATE;

@Log4j2
@RestController
public class OrderControllerRest {
    private static final Logger LOGGER = LogManager.getLogger(OrderControllerRest.class);

    @Autowired
    OrderService orderService;

    @Autowired
    CommissionService commissionService;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    WalletService walletService;

    @Autowired
    UserService userService;

    @Autowired
    private UserOperationService userOperationService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    LocaleResolver localeResolver;

    @Autowired
    StopOrderService stopOrderService;

    @RequestMapping("/order/submitnew/{orderType}")
    public OrderCreateSummaryDto newOrderToSell(@PathVariable OperationType orderType,
                                                Principal principal,
                                                BigDecimal amount,
                                                BigDecimal rate,
                                                OrderBaseType baseType,
                                                String currencyPair,
                                                @RequestParam(value = "stop", required = false) BigDecimal stop,
                                                HttpServletRequest request) {
        long before = System.currentTimeMillis();
        try {
            OrderCreateSummaryDto orderCreateSummaryDto;
            if (amount == null) amount = BigDecimal.ZERO;
            if (rate == null) rate = BigDecimal.ZERO;
            if (baseType == null) baseType = OrderBaseType.LIMIT;
            /* CurrencyPair activeCurrencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");*/
            CurrencyPair activeCurrencyPair = currencyService.getNotHiddenCurrencyPairByName(currencyPair);
            if (activeCurrencyPair == null) {
                throw new RuntimeException("Wrong currency pair");
            }
            if (baseType == OrderBaseType.STOP_LIMIT && stop == null) {
                throw new RuntimeException("Try to create stop-order without stop rate");
            }
            OrderCreateDto orderCreateDto = orderService.prepareNewOrder(activeCurrencyPair, orderType, principal.getName(), amount, rate, baseType);
            orderCreateDto.setOrderBaseType(baseType);
            orderCreateDto.setStop(stop);
            /**/
            OrderValidationDto orderValidationDto = orderService.validateOrder(orderCreateDto);
            Map<String, Object> errorMap = orderValidationDto.getErrors();
            orderCreateSummaryDto = new OrderCreateSummaryDto(orderCreateDto, localeResolver.resolveLocale(request));
            if (!errorMap.isEmpty()) {
                for (Map.Entry<String, Object> pair : errorMap.entrySet()) {
                    Object[] messageParams = orderValidationDto.getErrorParams().get(pair.getKey());
                    pair.setValue(messageSource.getMessage((String) pair.getValue(), messageParams, localeResolver.resolveLocale(request)));
                }
                errorMap.put("order", orderCreateSummaryDto);
                request.getSession().setAttribute("orderCreationError", errorMap);
                throw new OrderParamsWrongException();
            } else {
                /*protect orderCreateDto*/
                request.getSession().setAttribute("/order/submitnew/orderCreateDto", orderCreateDto);
            }
            return orderCreateSummaryDto;
        } catch (OrderParamsWrongException e) {
            long after = System.currentTimeMillis();
            LOGGER.error("error... ms: " + (after - before) + " : " + e + " " + request.getSession().getAttribute("orderCreationError"));
            throw e;
        } catch (Exception e) {
            long after = System.currentTimeMillis();
            LOGGER.error("error... ms: " + (after - before) + " : " + e);
            throw e;
        } finally {
            long after = System.currentTimeMillis();
            LOGGER.debug("completed... ms: " + (after - before));
        }

    }

    @RequestMapping(value = "/order/create", produces = "application/json;charset=utf-8")
    public String recordOrderToDB(HttpServletRequest request) {
        me.exrates.model.vo.ProfileData profileData = new me.exrates.model.vo.ProfileData(200);
        long before = System.currentTimeMillis();
        boolean accessToOperationForUser = userOperationService.getStatusAuthorityForUserByOperation(userService.getIdByEmail(request.getUserPrincipal().getName()), UserOperationAuthority.TRADING);
        if (!accessToOperationForUser) {
            throw new UserOperationAccessException(messageSource.getMessage("merchant.operationNotAvailable", null, localeResolver.resolveLocale(request)));
        }
        /*restore protected orderCreateDto*/
        OrderCreateDto orderCreateDto = (OrderCreateDto) request.getSession().getAttribute("/order/submitnew/orderCreateDto");
        try {
            if (orderCreateDto == null) {
                /*it may be if user twice click the create button from the current submit form. After first click orderCreateDto will be cleaned*/
                throw new OrderCreationException(messageSource.getMessage("order.recreateerror", null, localeResolver.resolveLocale(request)));
            }
            try {
                switch (orderCreateDto.getOrderBaseType()) {
                    case STOP_LIMIT: {
                        return stopOrderService.create(orderCreateDto, CREATE, localeResolver.resolveLocale(request));
                    }
                    default: {
                        return orderService.createOrder(orderCreateDto, CREATE, localeResolver.resolveLocale(request));
                    }
                }
            } catch (NotEnoughUserWalletMoneyException e) {
                throw new NotEnoughUserWalletMoneyException(messageSource.getMessage("validation.orderNotEnoughMoney", null, localeResolver.resolveLocale(request)));
            } catch (OrderCreationException e) {
                throw new OrderCreationException(messageSource.getMessage("order.createerror", new Object[]{e.getLocalizedMessage()}, localeResolver.resolveLocale(request)));
            } catch (NotCreatableOrderException e) {
                throw e;
            }
        } catch (Exception e) {
            long after = System.currentTimeMillis();
            LOGGER.error("error... ms: " + (after - before) + " :\n\t " + ExceptionUtils.getStackTrace(e));
            throw e;
        } finally {
            profileData.checkAndLog("slow creation order: " + orderCreateDto + " profile: " + profileData);
            long after = System.currentTimeMillis();
            /*clear orderCreateDto: it's necessary to prevent re-creating order from the current submit form */
            request.getSession().removeAttribute("/order/submitnew/orderCreateDto");
            LOGGER.debug("completed... ms: " + (after - before));
        }
    }

    @RequestMapping(value = "/order/accept", produces = "application/json;charset=utf-8")
    public String acceptOrder(@RequestBody String ordersListString, Principal principal, HttpServletRequest request) {
        long before = System.currentTimeMillis();
        boolean accessToOperationForUser = userOperationService.getStatusAuthorityForUserByOperation(userService.getIdByEmail(principal.getName()), UserOperationAuthority.TRADING);
        if (!accessToOperationForUser) {
            throw new UserOperationAccessException(messageSource.getMessage("merchant.operationNotAvailable", null, localeResolver.resolveLocale(request)));
        }
        try {
            List<Integer> ordersList = Arrays.asList(ordersListString.split(" ")).stream().map(e -> Integer.valueOf(e)).collect(Collectors.toList());
            try {
                int userId = userService.getIdByEmail(principal.getName());
                orderService.acceptOrdersList(userId, ordersList, localeResolver.resolveLocale(request));
            } catch (AttemptToAcceptBotOrderException e) {
                return "";
            } catch (Exception e) {
                throw e;
            }
            return "{\"result\":\"" + messageSource.getMessage("order.acceptsuccess", new Integer[]{ordersList.size()}, localeResolver.resolveLocale(request)) + "\"}";
        } catch (Exception e) {
            long after = System.currentTimeMillis();
            LOGGER.error("error... ms: " + (after - before) + " : " + e);
            throw e;
        } finally {
            long after = System.currentTimeMillis();
            LOGGER.debug("completed... ms: " + (after - before));
        }
    }


    @RequestMapping("/order/submitdelete/{orderId}")
    public OrderCreateSummaryDto submitDeleteOrder(@PathVariable Integer orderId,
                                                   @RequestParam(value = "baseType", defaultValue = "1") int typeId,
                                                   HttpServletRequest request, Principal principal) {
        long before = System.currentTimeMillis();
        OrderBaseType orderBaseType = OrderBaseType.convert(typeId);
        try {
            OrderCreateDto orderCreateDto;
            switch (orderBaseType) {
                case STOP_LIMIT: {
                    orderCreateDto = stopOrderService.getOrderById(orderId, false);
                    break;
                }
                default: {
                    orderCreateDto = orderService.getMyOrderById(orderId);
                }
            }
            if (orderCreateDto == null) {
                throw new OrderNotFoundException(messageSource.getMessage("orders.getordererror", new Object[]{orderId}, localeResolver.resolveLocale(request)));
            }
            String userEmail = userService.getEmailById(orderCreateDto.getUserId());
            if (principal == null || !principal.getName().equals(userEmail)) {
                throw new OrderCancellingException(messageSource.getMessage("myorders.deletefailed", null, localeResolver.resolveLocale(request)));
            }
            orderCreateDto.setOrderBaseType(orderBaseType);
            request.getSession().setAttribute("/order/submitdelete/orderCreateDto", orderCreateDto);
            return new OrderCreateSummaryDto(orderCreateDto, localeResolver.resolveLocale(request));
        } catch (Exception e) {
            long after = System.currentTimeMillis();
            LOGGER.error("error... ms: " + (after - before) + " : " + e);
            throw e;
        } finally {
            long after = System.currentTimeMillis();
            LOGGER.debug("completed... ms: " + (after - before));
        }
    }

    @RequestMapping(value = "/order/delete", produces = "application/json;charset=utf-8")
    public String deleteOrder(HttpServletRequest request) {
        long before = System.currentTimeMillis();
        try {
            OrderCreateDto orderCreateDto = (OrderCreateDto) request.getSession().getAttribute("/order/submitdelete/orderCreateDto");
            request.getSession().removeAttribute("/order/submitdelete/orderCreateDto");
            if (orderCreateDto == null) {
                throw new OrderCreationException(messageSource.getMessage("order.redeleteerror", null, localeResolver.resolveLocale(request)));
            }
            boolean result;
            switch (orderCreateDto.getOrderBaseType()) {
                case STOP_LIMIT: {
                    result = stopOrderService.cancelOrder(new ExOrder(orderCreateDto), localeResolver.resolveLocale(request));
                    break;
                }
                default: {
                    result = orderService.cancelOrder(new ExOrder(orderCreateDto), localeResolver.resolveLocale(request));
                }
            }
            if (!result) {
                throw new OrderCancellingException(messageSource.getMessage("myorders.deletefailed", null, localeResolver.resolveLocale(request)));
            }
            return "{\"result\":\"" + messageSource.getMessage("myorders.deletesuccess", null, localeResolver.resolveLocale(request)) + "\"}";
        } catch (Exception e) {
            long after = System.currentTimeMillis();
            LOGGER.error("error... ms: " + (after - before) + " : " + e);
            throw e;
        } finally {
            long after = System.currentTimeMillis();
            LOGGER.debug("completed... ms: " + (after - before));
        }
    }

    @RequestMapping(value = "/order/orderinfo", method = RequestMethod.GET)
    public OrderInfoDto getOrderInfo(@RequestParam int id, HttpServletRequest request) {
        long before = System.currentTimeMillis();
        try {
            return orderService.getOrderInfo(id, localeResolver.resolveLocale(request));
        } finally {
            long after = System.currentTimeMillis();
            LOGGER.debug("completed... ms: " + (after - before));
        }
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(OrderParamsWrongException.class)
    @ResponseBody
    public Map orderParamsWrongExceptionHandler(HttpServletRequest req, Exception exception) {
        return (Map) req.getSession().getAttribute("orderCreationError");
    }

    /*
    error handlers for this controller
    * */

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(NotEnoughMoneyException.class)
    @ResponseBody
    public ErrorInfo notEnoughMoneyExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(NotEnoughUserWalletMoneyException.class)
    @ResponseBody
    public ErrorInfo NotEnoughUserWalletMoneyExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(OrderCreationException.class)
    @ResponseBody
    public ErrorInfo OrderCreationExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(OrderAcceptionException.class)
    @ResponseBody
    public ErrorInfo OrderAcceptionExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(OrderCancellingException.class)
    @ResponseBody
    public ErrorInfo OrderCancellingExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(NotAcceptableOrderException.class)
    @ResponseBody
    public ErrorInfo NotAcceptableOrderExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(NotCreatableOrderException.class)
    @ResponseBody
    public ErrorInfo NotCreatableOrderExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(WrongFinPasswordException.class)
    @ResponseBody
    public ErrorInfo WrongFinPasswordExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(AbsentFinPasswordException.class)
    @ResponseBody
    public ErrorInfo AbsentFinPasswordExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(NotConfirmedFinPasswordException.class)
    @ResponseBody
    public ErrorInfo NotConfirmedFinPasswordExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseBody
    public ErrorInfo OrderNotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

}

