package com.scaffold.common.rest.databind;


import java.io.IOException;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JodaLocalDateJsonDeserializer extends JsonDeserializer<LocalDate> {

	@Override
	public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
			JsonProcessingException {
		String readValueAs = jp.readValueAs(String.class);
		return LocalDate.parse(readValueAs, DateTimeFormat.forPattern("yyyy-MM-dd"));
	}

   

}