package me.exrates.model.dto;

public enum PagingDirection {
    FORWARD(1),
    BACKWARD(-1);

    private final int direction;

    PagingDirection(int status) {
        this.direction = status;
    }

    public int getDirection() {
        return direction;
    }
}
