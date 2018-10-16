package me.exrates.dao;

import me.exrates.model.dto.RefFilterData;
import me.exrates.model.dto.ReferralInfoDto;
import me.exrates.model.dto.ReferralProfitDto;

import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface ReferralUserGraphDao {

    void create(int child, int parent);

    Integer getParent(Integer child);
    
    List<Integer> getChildrenForParentAndBlock(Integer parent);
    
    void changeReferralParent(Integer formerParent, Integer newParent);

    List<ReferralInfoDto> getInfoAboutFirstLevRefs(int userId, int profitUser, int limit, int offset, RefFilterData refFilterData);

    ReferralInfoDto getInfoAboutUserRef(int userId, int profitUser, RefFilterData filterData);

    List<ReferralProfitDto> detailedCountRefsTransactions(Integer userId, int profitUser, RefFilterData refFilterData);

    int getInfoAboutFirstLevRefsTotalSize(int parentId);
}
