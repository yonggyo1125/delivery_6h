package org.sparta.delivery.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@ToString
@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserId implements Serializable {
    @Column(length=45, name="user_id")
    private UUID id;

    public UserId(UUID id) {
        this.id = id;
    }

    public static UserId of(UUID id) {
        return new UserId(id);
    }
}