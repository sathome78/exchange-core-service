package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.stream.Collectors;

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
