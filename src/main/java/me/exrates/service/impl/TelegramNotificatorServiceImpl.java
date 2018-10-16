package me.exrates.service.impl;

import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.TelegramSubscriptionDao;
import me.exrates.exception.MessageUndeliweredException;
import me.exrates.exception.PaymentException;
import me.exrates.exception.TelegramSubscriptionException;
import me.exrates.model.dto.NotificatorSubscription;
import me.exrates.model.dto.TelegramSubscription;
import me.exrates.model.dto.WalletTransferStatus;
import me.exrates.model.enums.*;
import me.exrates.model.main.Currency;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.*;
import me.exrates.service.telegram.TelegramBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;
import java.util.StringJoiner;

import static me.exrates.model.vo.WalletOperationData.BalanceType.ACTIVE;

/**
 * Created by Maks on 29.09.2017.
 */
@Log4j2(topic = "message_notify")
@Component("telegramNotificatorServiceImpl")
public class TelegramNotificatorServiceImpl implements NotificatorService, Subscribable {

    @Autowired
    private TelegramSubscriptionDao subscribtionDao;
    @Autowired
    private UserService userService;
    @Autowired
    private TelegramBotService telegramBotService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private NotificatorsService notificatorsService;


    private Currency currency;
    private static final String CURRENCY_NAME_FOR_PAY = "USD";

    @PostConstruct
    private void init() {
        currency = currencyService.findByName(CURRENCY_NAME_FOR_PAY);
    }

    @Transactional
    @Override
    public Object subscribe(Object subscribeData) {
        TelegramSubscription subscriptionDto = (TelegramSubscription) subscribeData;
        String[] data = (subscriptionDto.getRawText()).split(":");
        String email = data[0];
        Optional<TelegramSubscription> subscriptionOptional = subscribtionDao.getSubscribtionByCodeAndEmail(subscriptionDto.getRawText(), email);
        TelegramSubscription subscription = subscriptionOptional.orElseThrow(TelegramSubscriptionException::new);
        NotificatorSubscriptionStateEnum nextState = subscription.getSubscriptionState().getNextState();
        if (subscription.getSubscriptionState().isFinalState()) {
            /*set New account for subscription if allready subscribed*/
            subscription.setChatId(subscriptionDto.getChatId());
            subscription.setUserAccount(subscriptionDto.getUserAccount());
            subscription.setCode(null);
        } else if (subscription.getSubscriptionState().isBeginState()) {
            subscription.setSubscriptionState(nextState);
            subscription.setChatId(subscriptionDto.getChatId());
            subscription.setUserAccount(subscriptionDto.getUserAccount());
            subscription.setCode(null);
        }
        subscribtionDao.updateSubscription(subscription);
        return null;
    }

    @Override
    public Object getSubscriptionByUserId(int userId) {
        return subscribtionDao.getSubscribtionByUserId(userId);
    }

    @Override
    @Transactional
    public String createSubscription(String userEmail) {
        String code = generateCode(userEmail);
        subscribtionDao.create(TelegramSubscription.builder()
                .userId(userService.getIdByEmail(userEmail))
                .subscriptionState(NotificatorSubscriptionStateEnum.getBeginState())
                .code(code).build());
        payForSubscribe(
                userEmail,
                OperationType.OUTPUT,
                getNotificationType().name().concat(":").concat(NotificationPayEventEnum.SUBSCRIBE.name())
        );
        return code;
    }

    @Override
    public NotificatorSubscription getSubscription(int userId) {
        return subscribtionDao.getSubscribtionByUserId(userId);
    }

    @Override
    public Object prepareSubscription(Object subscriptionObject) {
        return null;
    }

    @Override
    @Transactional
    public Object reconnect(Object object) {
        String userEmail = String.valueOf(object);
        TelegramSubscription subscription = subscribtionDao.getSubscribtionByUserId(userService.getIdByEmail(userEmail));
        Preconditions.checkState(subscription.getSubscriptionState().isFinalState());
        String code = generateCode(userEmail);
        subscribtionDao.updateCode(code, userService.getIdByEmail(userEmail));
        return code;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public String sendMessageToUser(String userEmail, String message, String subject) {
        Optional<TelegramSubscription> subscriptionOptional = Optional.ofNullable(subscribtionDao.getSubscribtionByUserId(userService.getIdByEmail(userEmail)));
        TelegramSubscription subscription = subscriptionOptional.orElseThrow(MessageUndeliweredException::new);
        if (!subscription.getSubscriptionState().isFinalState()) {
            throw new MessageUndeliweredException();
        }
        telegramBotService.sendMessage(subscription.getChatId(), message);
        return subscription.getUserAccount();
    }

    private String generateCode(String email) {
        return new StringJoiner(":").add(email).add(String.valueOf(100000000 + new Random().nextInt(100000000))).toString();
    }

    @Override
    public NotificationTypeEnum getNotificationType() {
        return NotificationTypeEnum.TELEGRAM;
    }

    @Transactional
    private BigDecimal payForSubscribe(String userEmail, OperationType operationType,
                                       String description) {
        int userId = userService.getIdByEmail(userEmail);
        UserRole role = userService.getUserRoleFromDB(userEmail);
        BigDecimal fee = notificatorsService.getSubscriptionPrice(getNotificationType().getCode(), role.getRole());
        if (fee.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        WalletOperationData walletOperationData = new WalletOperationData();
        walletOperationData.setCommissionAmount(fee);
        walletOperationData.setOperationType(operationType);
        walletOperationData.setWalletId(walletService.getWalletId(userId, currency.getId()));
        walletOperationData.setBalanceType(ACTIVE);
        walletOperationData.setAmount(fee);
        walletOperationData.setSourceType(TransactionSourceType.NOTIFICATIONS);
        walletOperationData.setDescription(description);
        WalletTransferStatus walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
        if (!walletTransferStatus.equals(WalletTransferStatus.SUCCESS)) {
            throw new PaymentException(walletTransferStatus);
        }
        return fee;
    }
}
