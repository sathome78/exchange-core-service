package me.exrates.controller.ngcontroller.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum VerificationDocumentType {

    @JsonProperty("PASSPORT")
    PASSPORT,
    @JsonProperty("IDENTITY_CARD")
    IDENTITY_CARD,
    @JsonProperty("DRIVER_LICENSE")
    DRIVER_LICENSE,
    @JsonProperty("PHOTO")
    PHOTO;

    @JsonCreator
    public static VerificationDocumentType of(String value) {
        if (value.equalsIgnoreCase(PASSPORT.toString())) {
            return PASSPORT;
        } else if (value.equalsIgnoreCase(IDENTITY_CARD.toString())) {
            return IDENTITY_CARD;
        } else if (value.equalsIgnoreCase(DRIVER_LICENSE.toString())) {
            return DRIVER_LICENSE;
        } else if (value.equalsIgnoreCase(PHOTO.toString())) {
            return PHOTO;
        }
        throw new RuntimeException("VerificationDocumentType error - " + value);
    }

    @JsonValue
    public String getValue() {
        return this.toString();
    }
}
