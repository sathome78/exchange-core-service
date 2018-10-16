package me.exrates.validator;

import me.exrates.model.Payment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Component
public class PaymentValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Payment.class.equals(clazz);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "meansOfPayment", "merchants.incorrectPaymentDetails");
        Payment payment = (Payment) o;
//        if (payment.getSum().compareTo(0.01) < 0) {
//            errors.reject("error","merchants.invalidSum");
//        }
    }
}