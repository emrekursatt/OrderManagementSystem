package com.tr.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.joda.time.DateTime;

import java.time.OffsetDateTime;
import java.time.OffsetTime;

@Getter
@Setter
@Entity
@Table(name = "payments")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private OrdersEntity orderEntity;

    @NotNull
    @Column(name = "payment_method", nullable = false, length = Integer.MAX_VALUE)
    private String paymentMethod;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Double amount;

    @NotNull
    @Column(name = "payment_date", nullable = false)
    private OffsetDateTime paymentDate;

}