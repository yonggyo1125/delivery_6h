package org.sparta.delivery.store.infrastructure.converter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.sparta.delivery.store.domain.ProductSubOption;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Converter(autoApply = true)
public class ProductSubOptionConverter implements AttributeConverter<List<ProductSubOption>, String> {


    private static final ObjectMapper om;
    static {
        om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
    }

    @Override
    public String convertToDatabaseColumn(List<ProductSubOption> attribute) {
        try {
            return attribute == null ? null : om.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("Converter Error : {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<ProductSubOption> convertToEntityAttribute(String dbData) {

        try {
            return StringUtils.hasText(dbData) ? om.readValue(dbData, new TypeReference<>() {}): List.of();
        } catch (JsonProcessingException e) {
            log.error("Converter Error : {}", e.getMessage(), e);
        }
        return List.of();
    }
}
