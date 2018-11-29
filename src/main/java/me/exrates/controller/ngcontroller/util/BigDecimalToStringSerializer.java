package me.exrates.controller.ngcontroller.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.exrates.util.BigDecimalProcessing;

import java.io.IOException;
import java.math.BigDecimal;

public class BigDecimalToStringSerializer  extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeString(BigDecimalProcessing.formatSpacePoint(value, false).replace(" ", ""));
    }
}
