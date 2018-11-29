package me.exrates.exception.security.exception;

import me.exrates.model.dto.PinDto;
import org.springframework.security.core.AuthenticationException;

/**
 * Created by maks on 02.07.2017.
 */
public class IncorrectPinException extends AuthenticationException {

    private final String REASON_CODE = "message.pin_code.incorrect";

    private PinDto dto;

    public IncorrectPinException(PinDto dto) {
        super(dto.getMessage());
        this.dto = dto;
    }

    public IncorrectPinException(String code) {
        super(code);
    }

    public PinDto getDto() {
        return dto;
    }

    public String getReason() {
        return REASON_CODE;
    }
}
