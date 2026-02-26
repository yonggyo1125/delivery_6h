package org.sparta.delivery.store.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Owner {
    @Column(length=45, name="owner_id", nullable = false)
    private UUID id;

    @Column(length=45, name="owner_name", nullable = false)
    private String name;

    @Builder
    protected Owner(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}
