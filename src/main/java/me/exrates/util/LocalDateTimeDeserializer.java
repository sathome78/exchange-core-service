package me.exrates.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * Created by Valk
 */
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
  @Override
  public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
    String str = jsonParser.readValueAsTree().toString().replaceAll("\"", "");
    if (str.endsWith("Z")) {
      return ZonedDateTime.parse(str).toLocalDateTime();
    } else {
      return LocalDateTime.parse(str);
    }
  }
}
