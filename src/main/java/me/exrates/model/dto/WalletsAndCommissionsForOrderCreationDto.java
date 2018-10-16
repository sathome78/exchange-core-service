package me.exrates.model.dto;

import java.math.BigDecimal;

/**
 * Contains
 * - IDs the currency
 * - IDs the wallets -
 * - balances the wallets of participants the order: user-creator, user-acceptor, company
 * - status the order
 *
 * @author ValkSam
 */
public class WalletsAndCommissionsForOrderCreationDto {
    int userId;
    /**/
    int spendWalletId;
    BigDecimal spendWalletActiveBalance;
    int commissionId;
    BigDecimal commissionValue;

    /*getters setters*/

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSpendWalletId() {
        return spendWalletId;
    }

    public void setSpendWalletId(int spendWalletId) {
        this.spendWalletId = spendWalletId;
    }

    public BigDecimal getSpendWalletActiveBalance() {
        return spendWalletActiveBalance;
    }

    public void setSpendWalletActiveBalance(BigDecimal spendWalletActiveBalance) {
        this.spendWalletActiveBalance = spendWalletActiveBalance;
    }

    public int getCommissionId() {
        return commissionId;
    }

    public void setCommissionId(int commissionId) {
        this.commissionId = commissionId;
    }

    public BigDecimal getCommissionValue() {
        return commissionValue;
    }

    public void setCommissionValue(BigDecimal commissionValue) {
        this.commissionValue = commissionValue;
    }
}
