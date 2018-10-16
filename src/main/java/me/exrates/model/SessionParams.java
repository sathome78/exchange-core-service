package me.exrates.model;

import lombok.Data;

@Data
public class SessionParams {

    private Integer id;
    private Integer userId;
    private int sessionTimeMinutes;
    private int sessionLifeTypeId;

    public SessionParams() {
    }

    public SessionParams(int sessionTimeMinutes, int sessionLifeType) {
        this.sessionTimeMinutes = sessionTimeMinutes;
        this.sessionLifeTypeId = sessionLifeType;
    }
}
