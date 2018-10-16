package me.exrates.model.main;

import me.exrates.model.User;
import me.exrates.util.BigDecimalProcessing;

import java.math.BigDecimal;

public class Wallet {
	
	private int id;
	private int currencyId;
	private User user;
	private BigDecimal activeBalance;
	private BigDecimal reservedBalance;
	private String name;

	public Wallet() {

	}

	public Wallet(int currencyId, User user, BigDecimal activeBalance) {
		this.currencyId = currencyId;
		this.user = user;
		this.activeBalance = activeBalance;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(int currencyId) {
		this.currencyId = currencyId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public BigDecimal getActiveBalance() {
		return BigDecimalProcessing.normalize(activeBalance);
	}

	public void setActiveBalance(BigDecimal activeBalance) {
		this.activeBalance = activeBalance;
	}

	public BigDecimal getReservedBalance() {
		return reservedBalance;
	}

	public void setReservedBalance(BigDecimal reservedBalance) {
		this.reservedBalance = reservedBalance;
	}

	/**
	 * Currently represents currency and balance on wallet
	 * 1,2,3 -> RUB,USD,EUR respectively
	 * any other value - BTC
	 * @return
     */
	public String getFullName() {
		final String activeBalance;
		switch (currencyId) {
			case 1:
			case 2:
			case 3:
				activeBalance = this.activeBalance.setScale(2,BigDecimal.ROUND_HALF_UP).toString();
				break;
			default:
				activeBalance = this.activeBalance.toString();
		}
		return name+" "+activeBalance;
	}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
	
			Wallet wallet = (Wallet) o;
	
			if (id != wallet.id) return false;
			if (currencyId != wallet.currencyId) return false;
			if (!user.equals(wallet.user)) return false;
			if (activeBalance != null ? !activeBalance.equals(wallet.activeBalance) : wallet.activeBalance != null) return false;
			if (reservedBalance != null ? !reservedBalance.equals(wallet.reservedBalance) : wallet.reservedBalance != null) return false;
			return name != null ? name.equals(wallet.name) : wallet.name == null;
	
		}

	@Override
	public int hashCode() {
		int result;
		result = id;
		result = 31 * result + currencyId;
		result = 31 * result + (activeBalance != null ? activeBalance.hashCode() : 0);
		result = 31 * result + (reservedBalance != null ? reservedBalance.hashCode() : 0);
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Wallet{" +
				"id=" + id +
				", currencyId=" + currencyId +
				", userId=" + user.getEmail() +
				", activeBalance=" + activeBalance +
				", reservedBalance=" + reservedBalance +
				", name='" + name + '\'' +
				'}';
	}
}