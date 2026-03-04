package org.sparta.delivery.payment.infrastructure.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.sparta.delivery.payment.domain.PaymentLog;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Converter(autoApply = true)
public class PaymentLogConverter implements AttributeConverter<List<PaymentLog>, String> {
    private static final ObjectMapper om;
    static {
        om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
    }


    @Override
    public String convertToDatabaseColumn(List<PaymentLog> attribute) {
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
    public List<PaymentLog> convertToEntityAttribute(String dbData) {
        if (StringUtils.hasText(dbData)) {
            try {
                return om.readValue(dbData, new TypeReference<>() {});
            } catch (JsonProcessingException e) {
                log.error("Converter Error: {}", e.getMessage(), e);
            }
        }

        return new ArrayList<>(); // 값이 없는 경우는 로그가 누적될 수 있도록 ArrayList로 초기화
    }
}
