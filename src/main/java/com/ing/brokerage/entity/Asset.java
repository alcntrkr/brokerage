package com.ing.brokerage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "ASSETS", uniqueConstraints = @UniqueConstraint(columnNames = {"CUSTOMER_ID","ASSET_NAME"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name="CUSTOMER_ID", nullable = false)
    private String customerId;

    @Column(name="ASSET_NAME", nullable = false)
    private String assetName;

    @Column(name = "SIZE")
    private Long size;

    @Column(name = "USABLE_SIZE")
    private Long usableSize;

    @Column(name="TRY_AMOUNT", precision=19, scale=2)
    private BigDecimal tryAmount; // monetary amount in major currency (e.g., TRY)

    public Asset(String customerId, String assetName, Long size, Long usableSize) {
        this.customerId = customerId; this.assetName = assetName; this.size = size; this.usableSize = usableSize;
    }
}
