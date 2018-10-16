package me.exrates.model;

import me.exrates.model.enums.ReferralTransactionStatusEnum;
import me.exrates.model.main.ExOrder;
import me.exrates.model.main.Transaction;

public class ReferralTransaction {

    private int id;
    private int userId;
    private int initiatorId;
    private ExOrder exOrder;
    private ReferralLevel referralLevel;
    private Transaction transaction;
    private String initiatorEmail;
    private ReferralTransactionStatusEnum statusEnum;

    public ExOrder getExOrder() {
        return exOrder;
    }

    public void setExOrder(final ExOrder exOrder) {
        this.exOrder = exOrder;
    }

    public ReferralLevel getReferralLevel() {
        return referralLevel;
    }

    public void setReferralLevel(final ReferralLevel referralLevel) {
        this.referralLevel = referralLevel;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(final Transaction transaction) {
        this.transaction = transaction;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(final int userId) {
        this.userId = userId;
    }

    public int getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(final int initiatorId) {
        this.initiatorId = initiatorId;
    }

    public String getInitiatorEmail() {
        return initiatorEmail;
    }

    public void setInitiatorEmail(final String initiatorEmail) {
        this.initiatorEmail = initiatorEmail;
    }

    public ReferralTransactionStatusEnum getStatusEnum() {
        return statusEnum;
    }

    public void setStatusEnum(ReferralTransactionStatusEnum statusEnum) {
        this.statusEnum = statusEnum;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ReferralTransaction that = (ReferralTransaction) o;

        if (id != that.id) return false;
        if (userId != that.userId) return false;
        if (initiatorId != that.initiatorId) return false;
        if (exOrder != null ? !exOrder.equals(that.exOrder) : that.exOrder != null) return false;
        if (referralLevel != null ? !referralLevel.equals(that.referralLevel) : that.referralLevel != null)
            return false;
        if (transaction != null ? !transaction.equals(that.transaction) : that.transaction != null) return false;
        return initiatorEmail != null ? initiatorEmail.equals(that.initiatorEmail) : that.initiatorEmail == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + userId;
        result = 31 * result + initiatorId;
        result = 31 * result + (exOrder != null ? exOrder.hashCode() : 0);
        result = 31 * result + (referralLevel != null ? referralLevel.hashCode() : 0);
        result = 31 * result + (transaction != null ? transaction.hashCode() : 0);
        result = 31 * result + (initiatorEmail != null ? initiatorEmail.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ReferralTransaction{" +
                "id=" + id +
                ", userId=" + userId +
                ", initiatorId=" + initiatorId +
                ", exOrder=" + exOrder +
                ", referralLevel=" + referralLevel +
                ", transaction=" + transaction +
                ", initiatorEmail='" + initiatorEmail + '\'' +
                '}';
    }
}
