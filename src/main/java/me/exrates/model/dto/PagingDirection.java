package me.exrates.model.dto;

/**
 * Created by Valk on 22.06.2016.
 */
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
