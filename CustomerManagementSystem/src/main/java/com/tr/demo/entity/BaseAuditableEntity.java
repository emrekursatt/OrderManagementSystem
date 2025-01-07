package com.tr.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class BaseAuditableEntity  extends BaseEntity  {

    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";

    @Column(name = CREATED_AT, updatable = false)
    @CreatedDate
    private Long createdAt;

    @Column(name = UPDATED_AT)
    @LastModifiedDate
    private Long updatedAt;
}
