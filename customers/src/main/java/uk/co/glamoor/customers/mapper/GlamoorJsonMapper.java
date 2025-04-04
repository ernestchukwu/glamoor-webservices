package uk.co.glamoor.customers.mapper;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.json.JsonMapper;


public class GlamoorJsonMapper {

	private static final JsonMapper mapper = new JsonMapper();
	private static final Logger logger = LoggerFactory.getLogger(GlamoorJsonMapper.class);

	public static <T> T fromJson(String json, Class<T> clazz) {
		try {
			mapper.registerModule(new JavaTimeModule());
			return mapper.readValue(json, clazz);
		} catch (JacksonException e) {
			logger.error("Error parsing json to object: ", e);
			return null;
		}
	}

	public static String toJson(Object object) {
		try {
			mapper.registerModule(new JavaTimeModule());
			return mapper.writeValueAsString(object);
		} catch (JacksonException e) {
			logger.error("Error parsing object to json: ", e);
			return null;
		}
	}
}
