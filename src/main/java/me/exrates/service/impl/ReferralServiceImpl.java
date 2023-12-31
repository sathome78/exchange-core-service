package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.ReferralLevelDao;
import me.exrates.dao.ReferralTransactionDao;
import me.exrates.dao.ReferralUserGraphDao;
import me.exrates.model.ReferralLevel;
import me.exrates.model.ReferralTransaction;
import me.exrates.model.User;
import me.exrates.model.dto.RefFilterData;
import me.exrates.model.dto.ReferralInfoDto;
import me.exrates.model.dto.ReferralProfitDto;
import me.exrates.model.dto.RefsListContainer;
import me.exrates.model.enums.*;
import me.exrates.model.main.*;
import me.exrates.model.main.Currency;
import me.exrates.model.onlineTableDto.MyReferralDetailedDto;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.*;
import me.exrates.util.BigDecimalProcessing;
import me.exrates.util.Cache;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static me.exrates.model.enums.OperationType.REFERRAL;
import static me.exrates.model.vo.WalletOperationData.BalanceType.ACTIVE;
import static me.exrates.util.BigDecimalProcessing.doAction;

@Service
@Log4j2
public class ReferralServiceImpl implements ReferralService {

    private static final int decimalPlaces = 9;
    @Autowired
    private ReferralLevelDao referralLevelDao;
    @Autowired
    private ReferralUserGraphDao referralUserGraphDao;
    @Autowired
    private ReferralTransactionDao referralTransactionDao;
    @Autowired
    private WalletService walletService;
    @Autowired
    private UserService userService;
    @Autowired
    private CompanyWalletService companyWalletService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private CommissionService commissionService;
    @Autowired
    private MessageSource messageSource;

    private Commission commission;
    private final BigDecimal HUNDREDTH = valueOf(100L);
    private @Value("${referral.url}") String referralUrl;

    @PostConstruct
    public void init() {
        this.commission = commissionService.getDefaultCommission(REFERRAL);
    }

    @Override
    public String generateReferral(final String userEmail) {
        final int userId = userService.getIdByEmail(userEmail);
        int prefix = new Random().nextInt(999 - 100 + 1) + 100;
        int suffix = new Random().nextInt(999 - 100 + 1) + 100;
        return referralUrl + prefix + userId + suffix;
    }

    @Override
    public Optional<Integer> reduceReferralRef(final String ref) {
        final String id = ref.substring(3).substring(0, ref.length() - 6);
        if (id.matches("[0-9]+")) {
            return Optional.of(Integer.valueOf(id));
        }
        return Optional.empty();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void processReferral(final ExOrder exOrder, final BigDecimal commissionAmount, Currency currency, int userId) {
        final List<ReferralLevel> levels = referralLevelDao.findAll();
        CompanyWallet cWallet = companyWalletService.findByCurrency(currency);
        Integer parent = null;
        for (ReferralLevel level : levels) {
            if (parent == null) {
                parent = referralUserGraphDao.getParent(userId);
            } else {
                parent = referralUserGraphDao.getParent(parent);
            }
            if (parent != null && !level.getPercent().equals(ZERO)) {
                final ReferralTransaction referralTransaction = new ReferralTransaction();
                referralTransaction.setExOrder(exOrder);
                referralTransaction.setReferralLevel(level);
                referralTransaction.setUserId(parent);
                referralTransaction.setInitiatorId(userId);
                int walletId = walletService.getWalletId(parent, currency.getId()); // Mutable variable
                if (walletId == 0) { // Wallet is absent, creating new wallet
                    final Wallet wallet = new Wallet();
                    wallet.setActiveBalance(ZERO);
                    wallet.setCurrencyId(currency.getId());
                    wallet.setUser(userService.getUserById(parent));
                    wallet.setReservedBalance(ZERO);
                    walletId = walletService.createNewWallet(wallet); // Changing mutable variable state
                }
                final ReferralTransaction createdRefTransaction = referralTransactionDao.create(referralTransaction);
                final BigDecimal amount = doAction(commissionAmount, level.getPercent(), ActionType.MULTIPLY_PERCENT);
                final WalletOperationData wod = new WalletOperationData();
                wod.setCommissionAmount(this.commission.getValue());
                wod.setCommission(this.commission);
                wod.setAmount(amount);
                wod.setWalletId(walletId);
                wod.setBalanceType(ACTIVE);
                wod.setOperationType(OperationType.INPUT);
                wod.setSourceType(TransactionSourceType.REFERRAL);
                wod.setSourceId(createdRefTransaction.getId());
                walletService.walletBalanceChange(wod);
                companyWalletService.withdrawReservedBalance(cWallet, amount);
                notificationService.createLocalizedNotification(parent, NotificationEvent.IN_OUT,
                        "referral.title", "referral.message",
                        new Object[]{BigDecimalProcessing.formatNonePoint(amount, false), currency.getName()});
            } else {
                break;
            }
        }
    }


    @Override
    public List<ReferralTransaction> findAll(final int userId) {
        return referralTransactionDao.findAll(userId);
    }

    @Override
    public List<ReferralLevel> findAllReferralLevels() {
        return referralLevelDao.findAll();
    }

    @Override
    public void bindChildAndParent(final int childUserId, final int parentUserId) {
        referralUserGraphDao.create(childUserId, parentUserId);
    }

    @Override
    public List<MyReferralDetailedDto> findAllMyReferral(CacheData cacheData, String email, Integer offset, Integer limit, Locale locale) {
        List<MyReferralDetailedDto> result = referralTransactionDao.findAllMyRefferal(email, offset, limit, locale);
        result.forEach(p -> {
            p.setStatus(messageSource.getMessage("message.ref." + p.getStatus(), null, locale));
        });
        if (Cache.checkCache(cacheData, result)) {
            result = new ArrayList<MyReferralDetailedDto>() {{
                add(new MyReferralDetailedDto(false));
            }};
        }
        return result;
    }

    @Override
    public void updateReferralParentForChildren(User user) {
        Integer userReferralParentId = getReferralParentId(user.getId());
        if (userReferralParentId == null) {
            userReferralParentId = userService.getCommonReferralRoot().getId();
        }
        log.debug(String.format("Changing ref parent from %s to %s", user.getId(), userReferralParentId));
        referralUserGraphDao.changeReferralParent(user.getId(), userReferralParentId);
    }

    @Override
    public Integer getReferralParentId(int childId) {
        return referralUserGraphDao.getParent(childId);
    }

    public RefsListContainer getRefsContainerForReq(String action, Integer userId, int profitUserId,
                                                    int onPage, int page, RefFilterData refFilterData) {
        int refLevel = 1;
        RefsListContainer container;
        RefActionType refActionType = RefActionType.convert(action);
        switch (refActionType) {
            case init: {
                container = this
                        .getUsersFirstLevelAndCountProfitForUser(profitUserId, profitUserId, onPage, page, refFilterData);
                container.setReferralProfitDtos(this.getAllUserRefProfit(null, profitUserId, refFilterData));
                break;
            }
            case search: {
                if (!StringUtils.isEmpty(refFilterData.getEmail())) {
                    userId = userService.getIdByEmail(refFilterData.getEmail());
                    refLevel = this.getUserReferralLevelForChild(userId, profitUserId);
                    if (refLevel == -1) {
                        return new RefsListContainer(Collections.emptyList());
                    }
                    container = this.getUsersRefToAnotherUser(userId, profitUserId, refLevel, refFilterData);
                    container.setReferralProfitDtos(this.getAllUserRefProfit(userId, profitUserId, refFilterData));
                } else {
                    container = this
                            .getUsersFirstLevelAndCountProfitForUser(profitUserId, profitUserId, onPage, page, refFilterData);
                    container.setReferralProfitDtos(this.getAllUserRefProfit(null, profitUserId, refFilterData));
                }
                break;
            }
            case toggle: {
                refLevel = this.getUserReferralLevelForChild(userId, profitUserId);
                if (refLevel >= 7 || refLevel < 0) {
                    return new RefsListContainer(Collections.emptyList());
                }
                container = this
                        .getUsersFirstLevelAndCountProfitForUser(userId, profitUserId, onPage, page, refFilterData);

                break;
            }
            default:
                return new RefsListContainer(Collections.emptyList());
        }
        container.setCurrentLevel(refLevel);
        return container;
    }

    private RefsListContainer getUsersFirstLevelAndCountProfitForUser(int userId, int profitForId, int onPage, int pageNumber, RefFilterData refFilterData) {
        int offset = (pageNumber - 1) * onPage;
        List<ReferralInfoDto> dtoList = referralUserGraphDao.getInfoAboutFirstLevRefs(userId, profitForId, onPage, offset, refFilterData);
        setDetailedAmountToDtos(dtoList, profitForId, refFilterData);
        int totalSize = referralUserGraphDao.getInfoAboutFirstLevRefsTotalSize(userId);
        log.warn("list size {}", dtoList.size());
        return new RefsListContainer(dtoList, onPage, pageNumber, totalSize);
    }

    private RefsListContainer getUsersRefToAnotherUser(int userId, int profitUser, int level, RefFilterData refFilterData) {
        List<ReferralInfoDto> dtoList = Arrays.asList(referralUserGraphDao.getInfoAboutUserRef(userId, profitUser, refFilterData));
        setDetailedAmountToDtos(dtoList, profitUser, refFilterData);
        return new RefsListContainer(dtoList, level);
    }

    private void setDetailedAmountToDtos(List<ReferralInfoDto> list, int profitUser, RefFilterData refFilterData) {
        list.stream().filter(p -> p.getRefProfitFromUser() > 0)
                .forEach(l -> l.setReferralProfitDtoList(referralUserGraphDao.detailedCountRefsTransactions(l.getRefId(), profitUser, refFilterData)));
    }


    private int getUserReferralLevelForChild(Integer childUserId, Integer parentUserId) {
        int i = 1;
        int level = -1;
        if (childUserId == null || childUserId.equals(0) || parentUserId == null) {
            return level;
        }
        if (childUserId.equals(parentUserId)) {
            return 0;
        }
        Integer parentId = referralUserGraphDao.getParent(childUserId);
        while (parentId != null && i <= 7) {
            if (parentId.equals(parentUserId)) {
                level = i;
                break;
            }
            parentId = referralUserGraphDao.getParent(parentId);
            i++;
        }
        return level;
    }

    private List<ReferralProfitDto> getAllUserRefProfit(Integer userId, Integer profitUserId, RefFilterData filterData) {
        return referralUserGraphDao.detailedCountRefsTransactions(userId, profitUserId, filterData);
    }

    private String refProfitString(List<ReferralProfitDto> list) {
        StringBuilder sb = new StringBuilder();
        list.forEach(i -> {
            sb.append(i.getAmount());
            sb.append(i.getCurrencyName());
            sb.append(", ");
        });
        sb.deleteCharAt(sb.lastIndexOf(","));
        return sb.toString();
    }

    private String getCSVTransactionsHeader() {
        return "Email;Amount;Referrals count;level";
    }

    @Override
    @Transactional
    public void setRefTransactionStatus(ReferralTransactionStatusEnum status, int refTransactionId) {
        referralTransactionDao.setRefTransactionStatus(status, refTransactionId);
    }
}