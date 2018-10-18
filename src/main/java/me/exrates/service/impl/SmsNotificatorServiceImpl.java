package me.exrates.service.impl;

import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.SmsSubscriptionDao;
import me.exrates.exception.*;
import me.exrates.model.dto.NotificatorSubscription;
import me.exrates.model.dto.SmsSubscriptionDto;
import me.exrates.model.dto.WalletTransferStatus;
import me.exrates.model.enums.*;
import me.exrates.model.epochta.EpochtaApi;
import me.exrates.model.epochta.Phones;
import me.exrates.model.main.CompanyWallet;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

import static me.exrates.model.vo.WalletOperationData.BalanceType.ACTIVE;
import static me.exrates.util.BigDecimalProcessing.doAction;

@Log4j2(topic = "message_notify")
@Component
public class SmsNotificatorServiceImpl implements NotificatorService, Subscribable {

    @Autowired
    private UserService userService;
    @Autowired
    private NotificatorsService notificatorsService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private CurrencyService currencyServiceImpl;
    @Autowired
    private SmsSubscriptionDao subscriptionDao;
    @Autowired
    private EpochtaApi smsService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CompanyWalletService companyWalletService;

    private static final String CURRENCY_NAME = "USD";
    private static final String SENDER = "Exrates";


    @Override
    public Object getSubscriptionByUserId(int userId) {
        return subscriptionDao.getByUserId(userId);
    }

    @Transactional
    private String sendRegistrationMessageToUser(String userEmail, String message) {
        int userId = userService.getIdByEmail(userEmail);
        int roleId = userService.getUserRoleFromDB(userId).getRole();
        BigDecimal messagePrice = notificatorsService.getMessagePrice(getNotificationType().getCode(), roleId);
        SmsSubscriptionDto subscriptionDto = subscriptionDao.getByUserId(userService.getIdByEmail(userEmail));
        pay(
                messagePrice,
                subscriptionDto.getNewPrice(),
                userId,
                getNotificationType().name().concat(":").concat(NotificationPayEventEnum.BUY_ONE.name())
        );
        send(subscriptionDto.getNewContact(), message);
        return String.valueOf(subscriptionDto.getContact());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public String sendMessageToUser(String userEmail, String message, String subject) throws MessageUndeliweredException {
        int userId = userService.getIdByEmail(userEmail);
        int roleId = userService.getUserRoleFromDB(userId).getRole();
        BigDecimal messagePrice = notificatorsService.getMessagePrice(getNotificationType().getCode(), roleId);
        SmsSubscriptionDto subscriptionDto = subscriptionDao.getByUserId(userService.getIdByEmail(userEmail));
        pay(
                messagePrice,
                subscriptionDto.getPrice(),
                userId,
                getNotificationType().name().concat(":").concat(NotificationPayEventEnum.BUY_ONE.name())
        );
        String xml = send(subscriptionDto.getContact(), message);
        try {
            BigDecimal cost = new BigDecimal(smsService.getValueFromXml(xml, "amount"));
            log.debug("last cost for number {} is {}", subscriptionDto.getContact(), cost);
            if (cost.compareTo(subscriptionDto.getPriceForContact()) != 0 && cost.compareTo(BigDecimal.ZERO) > 0) {
                subscriptionDao.updateDeliveryPrice(userId, cost);
            }
        } catch (Exception e) {
            log.error("can't get new price", e);
        }
        return String.valueOf(subscriptionDto.getContact());
    }

    @Transactional
    private String send(String contact, String message) {
        log.debug("send sms to {}, message {}", contact, message);
        String xml = smsService.sendSms(SENDER, message,
                new ArrayList<Phones>() {{
                    add(new Phones("id1", "", contact));
                }});

        log.debug("send sms status {}", xml);
        String status;
        try {
            status = smsService.getValueFromXml(xml, "status");
            if (Integer.parseInt(status) < 1) {
                throw new MessageUndeliweredException();
            }
        } catch (Exception e) {
            throw new MessageUndeliweredException();
        }
        return xml;
    }

    /*return sms cost for user and phone number*/
    @Override
    public Object prepareSubscription(Object subscriptionObject) {
        SmsSubscriptionDto subscriptionDto = (SmsSubscriptionDto) subscriptionObject;
        Map<String, String> phones = new HashMap<>();
        Preconditions.checkArgument(!StringUtils.isEmpty(subscriptionDto.getNewContact()));
        phones.put("id1", subscriptionDto.getNewContact());
        log.debug("contact {}", subscriptionDto.getNewContact());
        BigDecimal cost;
        String status;
        try {
            String xml = smsService.getPrice("text", phones);
            log.debug("response {}", xml);
            status = smsService.getValueFromXml(xml, "status");
            Preconditions.checkArgument(!status.equals("-1"));
            cost = new BigDecimal(smsService.getValueFromXml(xml, "amount"));
        } catch (Exception e) {
            throw new ServiceUnavailableException();
        }
        SmsSubscriptionDto oldDto = getByUserId(subscriptionDto.getUserId());
        if (oldDto != null) {
            subscriptionDto.setStateEnum(oldDto.getStateEnum());
            subscriptionDto.setPriceForContact(oldDto.getPriceForContact());
            subscriptionDto.setContact(oldDto.getContact());
        } else {
            subscriptionDto.setStateEnum(NotificatorSubscriptionStateEnum.getBeginState());
        }
        subscriptionDto.setNewPrice(cost);
        UserRole role = userService.getUserRoleFromDB(subscriptionDto.getUserId());
        BigDecimal feePercent = notificatorsService.getMessagePrice(getNotificationType().getCode(), role.getRole());
        createOrUpdate(subscriptionDto);
        return doAction(cost, doAction(cost, feePercent, ActionType.MULTIPLY_PERCENT), ActionType.ADD);
    }

    @Override
    public Object createSubscription(String email) {
        SmsSubscriptionDto subscriptionDto = getByUserId(userService.getIdByEmail(email));
        Preconditions.checkArgument(subscriptionDto.getNewContact() != null && subscriptionDto.getNewPrice() != null);
        subscriptionDto.setCode(generateCode());
        createOrUpdate(subscriptionDto);
        Locale locale = userService.getUserLocaleForMobile(email);
        try {
            sendRegistrationMessageToUser(email,
                    messageSource.getMessage("message.sms.codeForSubscribe", new String[]{subscriptionDto.getCode()}, locale));
        } catch (MessageUndeliweredException e) {
            log.error(e);
            throw new UnoperableNumberException();
        } catch (PaymentException e) {
            log.error(e);
            throw e;
        } catch (Exception e) {
            log.error(e);
            throw new ServiceUnavailableException();
        }
        return subscriptionDto;
    }

    @Transactional
    @Override
    public Object subscribe(Object subscriptionObject) {
        SmsSubscriptionDto recievedDto = (SmsSubscriptionDto) subscriptionObject;
        SmsSubscriptionDto userDto = Preconditions.checkNotNull(getByUserId(recievedDto.getUserId()));
        if (recievedDto.getCode().equals(userDto.getCode())) {
            userDto.setStateEnum(NotificatorSubscriptionStateEnum.getFinalState());
            userDto.setCode(null);
            userDto.setPriceForContact(userDto.getNewPrice());
            userDto.setContact(userDto.getNewContact());
            userDto.setNewPrice(null);
            userDto.setNewContact(null);
            createOrUpdate(userDto);
            return userDto;
        }
        throw new IncorrectSmsPinException();
    }

    private String generateCode() {
        return String.valueOf(10000 + new Random().nextInt(10000));
    }

    @Override
    public NotificatorSubscription getSubscription(int userId) {
        return subscriptionDao.getByUserId(userId);
    }

    @Transactional
    @Override
    public Object reconnect(Object subscriptionObject) {
        return subscribe(subscriptionObject);
    }

    @Override
    public NotificationTypeEnum getNotificationType() {
        return NotificationTypeEnum.SMS;
    }

    @Transactional
    private BigDecimal pay(BigDecimal feePercent, BigDecimal deliveryAmount, int userId, String description) {
        BigDecimal feeAmount = doAction(deliveryAmount, feePercent, ActionType.MULTIPLY_PERCENT);
        BigDecimal totalAmount = doAction(feeAmount, deliveryAmount, ActionType.ADD);
        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        WalletOperationData walletOperationData = new WalletOperationData();
        walletOperationData.setOperationType(OperationType.OUTPUT);
        walletOperationData.setWalletId(walletService.getWalletId(userId, currencyServiceImpl.findByName(CURRENCY_NAME).getId()));
        walletOperationData.setBalanceType(ACTIVE);
        walletOperationData.setCommissionAmount(feeAmount);
        walletOperationData.setAmount(totalAmount);
        walletOperationData.setSourceType(TransactionSourceType.NOTIFICATIONS);
        walletOperationData.setDescription(description);
        WalletTransferStatus walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
        if (!walletTransferStatus.equals(WalletTransferStatus.SUCCESS)) {
            throw new PaymentException(walletTransferStatus);
        }
        CompanyWallet companyWallet = companyWalletService.findByCurrency(currencyServiceImpl.findByName(CURRENCY_NAME));
        companyWalletService.deposit(companyWallet, new BigDecimal(0), feeAmount);
        return totalAmount;
    }

    private void createOrUpdate(SmsSubscriptionDto dto) {
        if (getByUserId(dto.getUserId()) == null) {
            subscriptionDao.create(dto);
        } else {
            subscriptionDao.update(dto);
        }
    }

    public SmsSubscriptionDto getByUserId(int userId) {
        return subscriptionDao.getByUserId(userId);
    }
}
