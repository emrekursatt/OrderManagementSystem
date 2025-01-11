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
@Table(name = "orders")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrdersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @NotNull
    @Column(name = "order_date", nullable = false)
    private OffsetDateTime orderDate;

    @NotNull
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

}