package me.exrates.model.enums;

import lombok.ToString;
import me.exrates.exception.UnsupportedInvoiceOperationDirectionException;

import java.util.stream.Stream;

@ToString
public enum InvoiceOperationDirection {
    REFILL(1), WITHDRAW(2), TRANSFER_VOUCHER(3);

    private int id;

    InvoiceOperationDirection(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static InvoiceOperationDirection convert(int id) {
        return Stream.of(InvoiceOperationDirection.values()).filter(item -> item.id == id).findFirst()
                .orElseThrow(() -> new UnsupportedInvoiceOperationDirectionException(String.format("id: %s", id)));
    }

    @Override
    public String toString() {
        return this.name();
    }
}
