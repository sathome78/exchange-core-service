package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Generic2faResponseDto {
    private String message;
    private String error;

    public Generic2faResponseDto(final String message) {
        super();
        this.message = message;
    }

}
