package uk.co.glamoor.bookings.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JacksonException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;


@Configuration
public class GlamoorJsonMapper {

	private static final Logger logger = LoggerFactory.getLogger(GlamoorJsonMapper.class);
	private static final ObjectMapper mapper = createObjectMapper();

	private static ObjectMapper createObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		JavaTimeModule module = new JavaTimeModule();

		// Format LocalDate as "yyyy-MM-dd"
		module.addSerializer(new LocalDateSerializer(DateTimeFormatter.ISO_DATE));

		// Format LocalTime as "HH:mm"
		module.addSerializer(new LocalTimeSerializer(DateTimeFormatter.ISO_TIME));

		objectMapper.registerModule(module);
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return objectMapper;
	}

	@Bean
	public ObjectMapper objectMapper() {
		return mapper;
	}

	public static <T> T fromJson(String json, Class<T> clazz) {
		try {
			return mapper.readValue(json, clazz);
		} catch (JacksonException e) {
			logger.error("Error parsing JSON to object: ", e);
			return null;
		}
	}

	public static String toJson(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (JacksonException e) {
			logger.error("Error parsing object to JSON: ", e);
			return null;
		}
	}
}
