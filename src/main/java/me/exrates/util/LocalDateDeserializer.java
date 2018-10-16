package me.exrates.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Created by Valk
 */
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
  @Override
  public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
    String str = jsonParser.readValueAsTree().toString().replaceAll("\"", "");
    return LocalDate.parse(str);
  }
}
