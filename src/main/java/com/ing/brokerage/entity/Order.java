package com.ing.brokerage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "ORDERS")
@Getter
@Setter
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "CUSTOMER_ID")
    private String customerId;

    @Column(name = "ASSET_NAME")
    private String assetName;

    @Enumerated(EnumType.STRING)
    @Column(name = "SIDE")
    private OrderSide side;

    @Column(name = "SIZE")
    private Long size; // shares or units

    @Column(name = "PRICE", precision = 19, scale = 2)
    private BigDecimal price; // price per share in major currency (e.g., 150.25)

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private OrderStatus status;

    @Column(name = "CREATE_DATE")
    private Instant createDate;

    public Order(String customerId, String assetName, OrderSide side, Long size, BigDecimal price, OrderStatus status) {
        this.customerId = customerId;
        this.assetName = assetName;
        this.side = side;
        this.size = size;
        this.price = price;
        this.status = status;
        this.createDate = Instant.now();
    }
}