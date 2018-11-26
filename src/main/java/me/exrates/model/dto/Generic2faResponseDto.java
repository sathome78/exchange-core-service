package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.stream.Collectors;

@Setter @Getter @ToString
public class Generic2faResponseDto {
    private String message;
    private String code;
    private String error;

    public Generic2faResponseDto(final String message, final String code) {
        this.message = message;
        this.code = code;
    }

    public Generic2faResponseDto(final String message, final String code, final String error) {
        super();
        this.message = message;
        this.error = error;
        this.code = code;
    }

    public Generic2faResponseDto(List<ObjectError> allErrors, String error) {
        this.error = error;
        String temp = allErrors.stream().map(e -> {
            if (e instanceof FieldError) {
                return "{\"field\":\"" + ((FieldError) e).getField() + "\",\"defaultMessage\":\"" + e.getDefaultMessage() + "\"}";
            } else {
                return "{\"object\":\"" + e.getObjectName() + "\",\"defaultMessage\":\"" + e.getDefaultMessage() + "\"}";
            }
        }).collect(Collectors.joining(","));
        this.message = "[" + temp + "]";
    }
}
