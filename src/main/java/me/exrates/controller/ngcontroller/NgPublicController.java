package me.exrates.controller.ngcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import me.exrates.controller.ngcontroller.model.OrderBookWrapperDto;
import me.exrates.controller.ngcontroller.model.ResponseInfoCurrencyPairDto;
import me.exrates.controller.ngcontroller.service.NgOrderService;
import me.exrates.exception.IllegalChatMessageException;
import me.exrates.model.ChatHistoryDateWrapperDto;
import me.exrates.model.StatisticForMarket;
import me.exrates.model.dto.ChatHistoryDto;
import me.exrates.model.enums.ChatLang;
import me.exrates.model.enums.IpTypesOfChecking;
import me.exrates.model.enums.OrderType;
import me.exrates.model.main.ChatMessage;
import me.exrates.model.main.CurrencyPair;
import me.exrates.service.ChatService;
import me.exrates.service.G2faService;
import me.exrates.service.IpBlockingService;
import me.exrates.service.MarketRatesHolder;
import me.exrates.service.OrderService;
import me.exrates.service.TelegramChatBotService;
import me.exrates.service.UserService;
import me.exrates.util.IpUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.springframework.util.ObjectUtils.isEmpty;


@RestController
@RequestMapping(value = "/info/public/v2/",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
)
public class NgPublicController {

    private static final Logger logger = LogManager.getLogger(NgPublicController.class);

    private final ChatService chatService;
    private final IpBlockingService ipBlockingService;
    private final UserService userService;
        private final SimpMessagingTemplate messagingTemplate;
    private final OrderService orderService;
    private final G2faService g2faService;
    private final NgOrderService ngOrderService;
    private final TelegramChatBotService telegramChatBotService;
    private final MarketRatesHolder marketRatesHolder;

    @Autowired
    public NgPublicController(ChatService chatService,
                              IpBlockingService ipBlockingService,
                              UserService userService,
                              SimpMessagingTemplate messagingTemplate,
                              OrderService orderService,
                              G2faService g2faService,
                              NgOrderService ngOrderService,
                              TelegramChatBotService telegramChatBotService1,
                              MarketRatesHolder marketRatesHolder) {
        this.chatService = chatService;
        this.ipBlockingService = ipBlockingService;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
        this.orderService = orderService;
        this.g2faService = g2faService;
        this.ngOrderService = ngOrderService;
        this.telegramChatBotService = telegramChatBotService1;
        this.marketRatesHolder = marketRatesHolder;
    }

    @GetMapping(value = "/if_email_exists")
    public ResponseEntity<Boolean> checkIfNewUserEmailExists(@RequestParam("email") String email, HttpServletRequest request) {
        Boolean unique = processIpBlocking(request, "email", email,
                () -> userService.ifEmailIsUnique(email));
        // we may use this elsewhere, so exists is opposite to unique
        return ResponseEntity.ok(!unique);
    }

    @GetMapping("/is_google_2fa_enabled")
    public ResponseEntity<Boolean> isGoogleTwoFAEnabled(@RequestParam("email") String email) {
        return ResponseEntity.ok(g2faService.isGoogleAuthenticatorEnable(email));
    }

    @GetMapping(value = "/if_username_exists")
    public ResponseEntity<Boolean> checkIfNewUserUsernameExists(@RequestParam("username") String username, HttpServletRequest request) {
        Boolean unique = processIpBlocking(request, "username", username,
                () -> userService.ifNicknameIsUnique(username));
        // we may use this elsewhere, so exists is opposite to unique
        return ResponseEntity.ok(!unique);
    }

    @GetMapping(value = "/chat/history")
    public ResponseEntity<List<ChatHistoryDateWrapperDto>> getChatMessages(final @RequestParam("lang") String lang) {
        try {
            List<ChatHistoryDto> msgs = Lists.newArrayList(telegramChatBotService.getMessages());
            return ResponseEntity.ok(Lists.newArrayList(new ChatHistoryDateWrapperDto(LocalDate.now(), msgs)));
        } catch (Exception e) {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @PostMapping("/chat")
    public ResponseEntity sendChatMessage(@RequestBody Map<String, String> body) {
        String language = body.getOrDefault("LANG", "EN");
        ChatLang chatLang = ChatLang.toInstance(language);
        String simpleMessage = body.get("MESSAGE");
        String email = body.getOrDefault("EMAIL", "");
        if (isEmpty(simpleMessage)) {
            return ResponseEntity.badRequest().build();
        }
        final ChatMessage message;
        try {
            message = chatService.persistPublicMessage(simpleMessage, email, chatLang);
        } catch (IllegalChatMessageException e) {
            return ResponseEntity.badRequest().build();
        }
        String destination = "/topic/chat/".concat(language.toLowerCase());
        messagingTemplate.convertAndSend(destination, fromChatMessage(message));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/open-orders/{pairId}/{precision}")
    public ResponseEntity<List<OrderBookWrapperDto>> getOpenOrders(@PathVariable Integer pairId, @PathVariable Integer precision) {
        return ResponseEntity.ok(ImmutableList.of(
                ngOrderService.findAllOrderBookItems(OrderType.BUY, pairId, precision),
                ngOrderService.findAllOrderBookItems(OrderType.SELL, pairId, precision)));
    }

    @GetMapping("/info/{currencyPairId}")
    public ResponseEntity<ResponseInfoCurrencyPairDto> getCurrencyPairInfo(@PathVariable int currencyPairId) {
        try {
            ResponseInfoCurrencyPairDto currencyPairInfo = ngOrderService.getCurrencyPairInfo(currencyPairId);
            return ResponseEntity.ok(currencyPairInfo);
        } catch (Exception e) {
            logger.error("Error - {}", e);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/currencies/fast")
    public ResponseEntity<List<StatisticForMarket>> getCurrencyPairInfoAll() {
        return ResponseEntity.ok(orderService.getAllCurrenciesMarkersForAllPairsModel());
    }

    @GetMapping("/currencies/fromdb")
    public ResponseEntity<List<StatisticForMarket>> getCurrencyPairInfoAllFromDb() {
        return ResponseEntity.ok(marketRatesHolder.getAllFromDb());
    }

    @GetMapping("/pair/{part}/{name}")
    public ResponseEntity<List<CurrencyPair>> getPairsByPartName(@PathVariable String name,
                                                                 @PathVariable String part) {
        List<CurrencyPair> result;
        if (part.equalsIgnoreCase("first")) {
            result = ngOrderService.getAllPairsByFirstPartName(name);
        } else {
            result = ngOrderService.getAllPairsBySecondPartName(name);
        }
        return ResponseEntity.ok(result);
    }

    private String fromChatMessage(ChatMessage message) {
        String send = "";
        ChatHistoryDto dto = new ChatHistoryDto();
        dto.setMessageTime(message.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        dto.setEmail(message.getNickname());
        dto.setBody(message.getBody());

        try {
            ObjectMapper mapper = new ObjectMapper();
            send = mapper.writeValueAsString(dto);
        } catch (Exception e) {
            logger.info("Failed to convert to json {}", dto.getBody());
        }
        return send;
    }

    private Boolean processIpBlocking(HttpServletRequest request, String logMessageValue,
                                      String value, Supplier<Boolean> operation) {
        String clientIpAddress = IpUtils.getClientIpAddress(request);
        ipBlockingService.checkIp(clientIpAddress, IpTypesOfChecking.OPEN_API);
        Boolean result = operation.get();
        if (!result) {
            ipBlockingService.failureProcessing(clientIpAddress, IpTypesOfChecking.OPEN_API);
            logger.debug("New user's %s %s is already stored!", logMessageValue, value);
        } else {
            ipBlockingService.successfulProcessing(clientIpAddress, IpTypesOfChecking.OPEN_API);
            logger.debug("New user's %s %s is not stored yet!", logMessageValue, value);
        }
        return result;
    }
}
