package me.exrates.model.enums;

import java.util.stream.Stream;

public enum UserStatus {

	REGISTERED(1),
	ACTIVE(2),
	DELETED(3),
    BANNED_IN_CHAT(4);

    private final int status;

    UserStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return this.name();
    }

    public static UserStatus convert(int status) {
        return Stream.of(UserStatus.values()).filter(item -> item.getStatus() == status).findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
