package me.exrates.model.enums;


import me.exrates.exception.UnsupportedOperationTypeException;

public enum TokenType {
    REGISTRATION(1),
    CHANGE_PASSWORD(2),
    CHANGE_FIN_PASSWORD(3),
    CONFIRM_NEW_IP(4);


    private final int tokenType;

    TokenType(int tokenType) {
        this.tokenType = tokenType;
    }

    public int getTokenType() {
        return tokenType;
    }

    public static TokenType convert(int tupleId) {
        switch (tupleId) {
            case 1:
                return REGISTRATION;
            case 2:
                return CHANGE_PASSWORD;
            case 3:
                return CHANGE_FIN_PASSWORD;
            case 4:
                return CONFIRM_NEW_IP;
            default:
                throw new UnsupportedOperationTypeException(tupleId);
        }
    }
}
