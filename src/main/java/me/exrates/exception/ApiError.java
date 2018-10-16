package me.exrates.exception;

import me.exrates.exception.security.exception.ErrorCode;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.stream.Collectors;

public class ApiError {
    private final ErrorCode errorCode;
    public final String url;
    public final String cause;
    public final String detail;

    public ApiError(ErrorCode errorCode, CharSequence url, Throwable ex) {
        this.errorCode = errorCode;
        this.url = url.toString();
        this.cause = ex.getClass().getSimpleName();
        String detail = ex.getLocalizedMessage();
        while (ex.getCause() != null) ex = ex.getCause();
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) ex;
            List<ObjectError> errors = exception.getBindingResult().getAllErrors();
            if (!errors.isEmpty()) {
                this.detail = exception.getBindingResult().getFieldErrors().stream()
                        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)).toString();
            } else {
                this.detail = ex.getLocalizedMessage() == null ? detail : ex.getLocalizedMessage();
            }

        } else {
            this.detail = ex.getLocalizedMessage() == null ? detail : ex.getLocalizedMessage();
        }

    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }


}
