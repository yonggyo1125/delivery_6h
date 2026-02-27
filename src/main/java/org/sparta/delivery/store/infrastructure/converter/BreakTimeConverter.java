package org.sparta.delivery.store.infrastructure.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.sparta.delivery.store.domain.BreakTime;
import org.springframework.util.StringUtils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Converter(autoApply = true)
public class BreakTimeConverter implements AttributeConverter<BreakTime, String> {

    private DateTimeFormatter formatter;

    public BreakTimeConverter() {
        formatter = DateTimeFormatter.ofPattern("HH:mm");
    }

    @Override
    public String convertToDatabaseColumn(BreakTime attribute) {

        LocalTime start = attribute.start();
        LocalTime end = attribute.end();
        if (start == null || end == null) return null;

        return "%s-%s".formatted(formatter.format(start), formatter.format(end));
    }

    @Override
    public BreakTime convertToEntityAttribute(String dbData) {
        if (!StringUtils.hasText(dbData)) return null;

        String[] data = dbData.split("-");
        if (data.length == 2) {
            try {
                return new BreakTime(LocalTime.parse(data[0], formatter), LocalTime.parse(data[1], formatter));
            } catch (Exception e) {
                log.error("Converter Error : {}", e.getMessage(), e);
                return null;
            }
        }

        return null;
    }
}
