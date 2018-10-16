package me.exrates.util;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Created by OLEG on 07.09.2016.
 */
public class LocalDateTimeToLongSerializer extends JsonSerializer<LocalDateTime> {


    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeNumber(Timestamp.valueOf(value).getTime());
    }
}
