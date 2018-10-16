package me.exrates.model.enums;

import me.exrates.exception.UnsupportedOperationPermissionException;

import java.util.stream.Stream;

public enum InvoiceOperationPermission {
    NONE(0), VIEW_ONLY(1), ACCEPT_DECLINE(2);

    private int code;

    InvoiceOperationPermission(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static InvoiceOperationPermission convert(int id) {
        return Stream.of(InvoiceOperationPermission.class.getEnumConstants())
                .filter(e -> e.code == id)
                .findAny()
                .orElseThrow(() -> new UnsupportedOperationPermissionException(String.valueOf(id)));
    }

    @Override
    public String toString() {
        return this.name();
    }
}
