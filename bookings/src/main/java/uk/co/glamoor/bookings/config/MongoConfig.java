package uk.co.glamoor.bookings.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

@Configuration
public class MongoConfig {
    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(Arrays.asList(
                // Converters for LocalDate
                new LocalDateToDateConverter(),
                new DateToLocalDateConverter(),

                // Converters for LocalTime
                new LocalTimeToStringConverter(),
                new StringToLocalTimeConverter()
        ));
    }

    @WritingConverter
    public static class LocalDateToDateConverter implements org.springframework.core.convert.converter.Converter<LocalDate, Date> {
        @Override
        public Date convert(LocalDate source) {
            return java.sql.Date.valueOf(source);
        }
    }

    // MongoDB Date to LocalDate
    @ReadingConverter
    public static class DateToLocalDateConverter implements org.springframework.core.convert.converter.Converter<Date, LocalDate> {
        @Override
        public LocalDate convert(Date source) {
            return source.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        }
    }

    // LocalTime to MongoDB String
    @WritingConverter
    public static class LocalTimeToStringConverter implements org.springframework.core.convert.converter.Converter<LocalTime, String> {
        @Override
        public String convert(LocalTime source) {
            return source.toString();
        }
    }

    // MongoDB String to LocalTime
    @ReadingConverter
    public static class StringToLocalTimeConverter implements org.springframework.core.convert.converter.Converter<String, LocalTime> {
        @Override
        public LocalTime convert(String source) {
            return LocalTime.parse(source);
        }
    }
}
