package org.sparta.delivery.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.*;


@Embeddable
@ToString @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryInfo {
    @Column(name="delivery_address", length=100)
    private String address;

    @Column(name="delivery_address_detail", length=100)
    private String addressDetail;

    @Lob
    @Column(name = "delivery_memo")
    private String memo;

    @Builder
    protected DeliveryInfo(String address, String addressDetail, String memo) {

    }
}
