package me.exrates.dao;

import me.exrates.model.ReferralTransaction;
import me.exrates.model.enums.ReferralTransactionStatusEnum;
import me.exrates.model.onlineTableDto.MyReferralDetailedDto;

import java.util.List;
import java.util.Locale;

public interface ReferralTransactionDao {

    List<ReferralTransaction> findAll(int userId);

    List<ReferralTransaction> findAll(int userId, int offset, int limit);

    ReferralTransaction create(ReferralTransaction referralTransaction);

    List<MyReferralDetailedDto> findAllMyRefferal(String email, Integer offset, Integer limit, Locale locale);

    void setRefTransactionStatus(ReferralTransactionStatusEnum status, int refTransactionId);
}
