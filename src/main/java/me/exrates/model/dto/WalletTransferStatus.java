package me.exrates.model.dto;

/**
 * Created by Valk on 23.05.2016.
 */
public enum WalletTransferStatus {
    SUCCESS,
    WALLET_NOT_FOUND,
    CORRESPONDING_COMPANY_WALLET_NOT_FOUND,
    CAUSED_NEGATIVE_BALANCE,
    WALLET_UPDATE_ERROR,
    TRANSACTION_CREATION_ERROR,
    TRANSACTION_UPDATE_ERROR
}
