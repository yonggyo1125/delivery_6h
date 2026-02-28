package org.sparta.delivery.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@ToString @Getter
@NoArgsConstructor(access =  AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductInfo {
    @Column(length=45, name="item_code", nullable = false)
    private String code;

    @Column(length=100, name="item_name", nullable = false)
    private String name;
}
