package com.challenge.voteconsumer.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@ReadingConverter
public class ZonedDateTimeReadConverter implements Converter<String, ZonedDateTime> {
    @Override
    public ZonedDateTime convert(String source) {
        return ZonedDateTime.parse(source, DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }
}
