package com.tr.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "customer")
public class CustomerEntity extends BaseAuditableEntity {

    @Size(max = 100)
    @NotNull
    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @Size(max = 255)
    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "tier_id", nullable = false)
    private TiersEntity tiersEntity;

    @Column(name = "order_count")
    private Integer orderCount;

    @Column(name = "password" )
    private String password;

    @Column(name = "enabled" , nullable = false )
    private Boolean enabled;

    @Column(name = "status" ,nullable = false)
    private int status;

    @Column(name = "fail_login_count" )
    private Integer failLoginCount;

    @OneToMany(mappedBy = "customerEntity")
    private Set<TiersHistoryEntity> tiersHistories = new LinkedHashSet<>();

}