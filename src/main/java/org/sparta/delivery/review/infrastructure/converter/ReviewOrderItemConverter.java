package org.sparta.delivery.review.infrastructure.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.sparta.delivery.store.domain.ReviewOrderItem;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Converter(autoApply = true)
public class ReviewOrderItemConverter implements AttributeConverter<List<ReviewOrderItem>, String> {

    private static final ObjectMapper om = new ObjectMapper();


    @Override
    public String convertToDatabaseColumn(List<ReviewOrderItem> attribute) {
        if (attribute != null && !attribute.isEmpty()) {
            try {
                return om.writeValueAsString(attribute);
            } catch (JsonProcessingException e) {
                log.error("Converter Error: {}", e.getMessage(), e);
            }
        }
        return null;
    }

    @Override
    public List<ReviewOrderItem> convertToEntityAttribute(String dbData) {

        if (StringUtils.hasText(dbData)) {
            try {
                return om.readValue(dbData, new TypeReference<>() {});
            } catch (JsonProcessingException e) {
                log.error("Converter Error: {}", e.getMessage(), e);
            }
        }

        return new ArrayList<>();
    }
}
