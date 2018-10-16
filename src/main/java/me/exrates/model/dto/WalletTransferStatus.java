package me.exrates.model.dto;

public enum WalletTransferStatus {
    SUCCESS,
    WALLET_NOT_FOUND,
    CORRESPONDING_COMPANY_WALLET_NOT_FOUND,
    CAUSED_NEGATIVE_BALANCE,
    WALLET_UPDATE_ERROR,
    TRANSACTION_CREATION_ERROR,
    TRANSACTION_UPDATE_ERROR
}
