package org.sparta.delivery.global.infrastructure.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.sparta.delivery.global.domain.Price;

import java.util.Objects;


@Converter(autoApply = true) // 전역에 자동 적용
public class PriceConverter implements AttributeConverter<Price, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Price price) {
        return price == null ? 0 : price.getValue();
    }

    @Override
    public Price convertToEntityAttribute(Integer value) {
        return new Price(Objects.requireNonNullElse(value, 0));
    }
}
