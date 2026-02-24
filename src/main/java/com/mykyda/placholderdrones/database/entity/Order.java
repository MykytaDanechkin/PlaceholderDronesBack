package com.mykyda.placholderdrones.database.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String receiverEmail;

    @Column(precision = 9, scale = 6, nullable = false)
    private BigDecimal targetLatitude;

    @Column(precision = 9, scale = 6, nullable = false)
    private BigDecimal targetLongitude;

    @Column(precision = 10, scale = 2)
    private BigDecimal distance;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDate placedAt = LocalDate.now();

    private LocalDate deliveredAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KitType kitType;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private int deliveryProgress;
}
