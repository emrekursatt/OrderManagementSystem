package com.tr.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tiers_history")
public class TiersHistoryEntity extends BaseEntity{

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customerEntitiy;

    @NotNull
    @Column(name = "previous_tier_id", nullable = false)
    private Integer previousTierEntitiy;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "new_tier_id", nullable = false)
    private TierEntity newTierEntitiy;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "change_date")
    private OffsetTime changeDate;

}