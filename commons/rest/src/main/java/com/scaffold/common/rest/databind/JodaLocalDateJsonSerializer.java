package com.scaffold.common.rest.databind;

import java.io.IOException;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class JodaLocalDateJsonSerializer extends JsonSerializer<LocalDate> {

    private static final String dateFormat = ("yyyy-MM-dd");

    @Override
    public void serialize(LocalDate date, JsonGenerator gen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        String formattedDate = DateTimeFormat.forPattern(dateFormat).print(date);

        gen.writeString(formattedDate);
    }

}