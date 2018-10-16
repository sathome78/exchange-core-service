package me.exrates.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.exrates.model.chart.ChartResolution;

import java.io.IOException;

public class ChartResolutionSerializer extends JsonSerializer<ChartResolution> {
    @Override
    public void serialize(ChartResolution value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeString(value.toString());
    }
}
