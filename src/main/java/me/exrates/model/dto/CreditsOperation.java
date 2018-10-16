package me.exrates.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.exrates.model.User;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.main.Commission;
import me.exrates.model.main.Currency;
import me.exrates.model.main.Merchant;
import me.exrates.model.main.Wallet;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.Optional;

@Getter
public class CreditsOperation {

  private final User user;
  private final BigDecimal origAmountAtCreationRequest;
  private final BigDecimal amount;
  private final BigDecimal commissionAmount;
  private final OperationType operationType;
  private final Commission commission;
  private final Currency currency;
  private final Wallet wallet;
  private final Merchant merchant;
  private final BigDecimal merchantCommissionAmount;
  private final Optional<String> destination;
  private final Optional<String> destinationTag;
  private final TransactionSourceType transactionSourceType;
  private Boolean generateAdditionalRefillAddressAvailable;
  private Boolean storeSameAddressForParentAndTokens;
  private final User recipient;
  private final Wallet recipientWallet;

  private CreditsOperation(Builder builder) {
    this.user = builder.user;
    this.origAmountAtCreationRequest = builder.origAmountAtCreationRequest;
    this.amount = builder.amount;
    this.wallet = builder.wallet;
    this.commissionAmount = builder.commissionAmount;
    this.operationType = builder.operationType;
    this.commission = builder.commission;
    this.currency = builder.currency;
    this.merchant = builder.merchant;
    this.merchantCommissionAmount = builder.merchantCommissionAmount;
    this.destination = builder.destination == null ?
        Optional.empty() : builder.destination;
    this.destinationTag = builder.destinationTag == null ?
        Optional.empty() : builder.destinationTag;
    this.transactionSourceType = builder.transactionSourceType;
    this.recipient = builder.recipient;
    this.recipientWallet = builder.recipientWallet;
  }


  public static class Builder {

    private User user;
    private BigDecimal origAmountAtCreationRequest;
    private BigDecimal amount;
    private BigDecimal commissionAmount;
    private OperationType operationType;
    private Commission commission;
    private Currency currency;
    private Wallet wallet;
    private Merchant merchant;
    private BigDecimal merchantCommissionAmount;
    private Optional<String> destination;
    private Optional<String> destinationTag;
    private TransactionSourceType transactionSourceType;
    private Boolean generateAdditionalRefillAddressAvailable;
    private Boolean storeSameAddressForParentAndTokens;
    private User recipient;
    private Wallet recipientWallet;

    public Builder user(User user) {
      this.user = user;
      return this;
    }

    public Builder initialAmount(BigDecimal fullAmount) {
      this.origAmountAtCreationRequest = fullAmount;
      return this;
    }

    public Builder amount(BigDecimal amount) {
      this.amount = amount;
      return this;
    }

    public Builder commissionAmount(BigDecimal commissionAmount) {
      this.commissionAmount = commissionAmount;
      return this;
    }

    public Builder operationType(OperationType operationType) {
      this.operationType = operationType;
      return this;
    }

    public Builder commission(Commission commission) {
      this.commission = commission;
      return this;
    }

    public Builder currency(Currency currency) {
      this.currency = currency;
      return this;
    }

    public Builder wallet(Wallet wallet) {
      this.wallet = wallet;
      return this;
    }

    public Builder merchant(Merchant merchant) {
      this.merchant = merchant;
      return this;
    }

    public Builder destination(String destination) {
      this.destination = Optional.ofNullable(destination);
      return this;
    }

    public Builder destinationTag(String destinationTag) {
      this.destinationTag = Optional.ofNullable(destinationTag);
      return this;
    }

    public Builder transactionSourceType(TransactionSourceType transactionSourceType) {
      this.transactionSourceType = transactionSourceType;
      return this;
    }

    public Builder recipient(User recipient) {
      this.recipient = recipient;
      return this;
    }

    public Builder recipientWallet(Wallet recipientWallet) {
      this.recipientWallet = recipientWallet;
      return this;
    }

    public Builder merchantCommissionAmount(BigDecimal merchantCommissionAmount) {
      this.merchantCommissionAmount = merchantCommissionAmount;
      return this;
    }

    public CreditsOperation build() {
      return new CreditsOperation(this);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    CreditsOperation that = (CreditsOperation) o;

    if (user != null ? !user.equals(that.user) : that.user != null) return false;
    if (origAmountAtCreationRequest != null ? !origAmountAtCreationRequest.equals(that.origAmountAtCreationRequest) : that.origAmountAtCreationRequest != null)
      return false;
    if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
    if (commissionAmount != null ? !commissionAmount.equals(that.commissionAmount) : that.commissionAmount != null)
      return false;
    if (operationType != that.operationType) return false;
    if (commission != null ? !commission.equals(that.commission) : that.commission != null) return false;
    if (currency != null ? !currency.equals(that.currency) : that.currency != null) return false;
    if (merchant != null ? !merchant.equals(that.merchant) : that.merchant != null) return false;
    if (destination != null ? !destination.equals(that.destination) : that.destination != null) return false;
    return transactionSourceType != null ? transactionSourceType.equals(that.transactionSourceType) : that.transactionSourceType == null;

  }

  @Override
  public int hashCode() {
    int result = user != null ? user.hashCode() : 0;
    result = 31 * result + (origAmountAtCreationRequest != null ? origAmountAtCreationRequest.hashCode() : 0);
    result = 31 * result + (amount != null ? amount.hashCode() : 0);
    result = 31 * result + (commissionAmount != null ? commissionAmount.hashCode() : 0);
    result = 31 * result + (operationType != null ? operationType.hashCode() : 0);
    result = 31 * result + (commission != null ? commission.hashCode() : 0);
    result = 31 * result + (currency != null ? currency.hashCode() : 0);
    result = 31 * result + (merchant != null ? merchant.hashCode() : 0);
    result = 31 * result + (destination != null ? destination.hashCode() : 0);
    result = 31 * result + (transactionSourceType != null ? transactionSourceType.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "CreditsOperation{" +
        "user=" + user +
        ", origAmountAtCreationRequest=" + origAmountAtCreationRequest +
        ", amount=" + amount +
        ", commissionAmount=" + commissionAmount +
        ", operationType=" + operationType +
        ", commission=" + commission +
        ", currency=" + currency +
        ", merchant=" + merchant +
        ", destination=" + destination +
        ", recipient=" + recipient +
        ", transactionSourceType=" + transactionSourceType +
        '}';
  }
}