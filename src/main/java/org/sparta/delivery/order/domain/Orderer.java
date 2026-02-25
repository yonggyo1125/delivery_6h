package org.sparta.delivery.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Getter
@ToString
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Orderer {
    @Column(length=45, name="orderer_id", nullable = false)
    private UUID id;

    @Column(length=45, name="orderer_name", nullable = false)
    private String name;

    @Column(length=65, name="orderer_email", nullable = false)
    private String email;

    @Builder
    protected Orderer(UUID id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
