package me.exrates.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ColorScheme {

    DARK, LIGHT;

    @JsonCreator
    public static ColorScheme of(String value) {
        if (value.equalsIgnoreCase(DARK.toString())) {
            return DARK;
        }
        return LIGHT;
    }

    @JsonValue
    public String getValue() {
        return this.toString();
    }
}