package me.exrates.exception;

import me.exrates.model.dto.WalletTransferStatus;

public class PaymentException extends RuntimeException {
    public PaymentException(WalletTransferStatus walletTransferStatus) {
//        super(walletTransferStatus.name());
    }
}
