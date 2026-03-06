package org.sparta.delivery.global.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.sparta.delivery.global.domain.service.UserDetails;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@MappedSuperclass
@Access(AccessType.FIELD)
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseUserEntity extends BaseEntity {
    @CreatedBy
    @Column(length=45, updatable = false)
    protected String createdBy;

    @LastModifiedBy
    @Column(length=45, insertable = false)
    protected String modifiedBy;

    @Column(length=45, insertable = false)
    protected UUID deletedBy;

    protected void delete(UserDetails userDetails) {
        deletedBy = userDetails.getId();
        deletedAt = LocalDateTime.now();
    }
}