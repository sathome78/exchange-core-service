package me.exrates.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by OLEG on 20.03.2017.
 */
public class BigDecimalNonePointSerializer extends JsonSerializer<BigDecimal> {
  
  @Override
  public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
    gen.writeString(BigDecimalProcessing.formatNonePoint(value, true));
  }
}
