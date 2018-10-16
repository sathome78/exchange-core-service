package me.exrates.service;

import me.exrates.model.ReferralLevel;
import me.exrates.model.ReferralTransaction;
import me.exrates.model.User;
import me.exrates.model.dto.RefFilterData;
import me.exrates.model.dto.RefsListContainer;
import me.exrates.model.enums.ReferralTransactionStatusEnum;
import me.exrates.model.main.CacheData;
import me.exrates.model.main.Currency;
import me.exrates.model.main.ExOrder;
import me.exrates.model.onlineTableDto.MyReferralDetailedDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface ReferralService {
    void processReferral(ExOrder exOrder, BigDecimal commissionFixedAmount, Currency currency, int userId);

    void setRefTransactionStatus(ReferralTransactionStatusEnum deleted, int sourceId);

    Integer getReferralParentId(int childId);

    RefsListContainer getRefsContainerForReq(String action, Integer userId, int profitUserId,
                                             int onPage, int page, RefFilterData refFilterData);

    List<ReferralLevel> findAllReferralLevels();

    Optional<Integer> reduceReferralRef(String refReference);

    String generateReferral(String name);

    void bindChildAndParent(int child, int parent);

    List<ReferralTransaction> findAll(int id);

    List<MyReferralDetailedDto> findAllMyReferral(CacheData cacheData, String email, Integer offset, Integer limit, Locale resolveLocale);

    void updateReferralParentForChildren(User user);
}
