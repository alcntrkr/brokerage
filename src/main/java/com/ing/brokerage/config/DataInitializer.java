package com.ing.brokerage.config;

import com.ing.brokerage.entity.Asset;
import com.ing.brokerage.repository.AssetRepository;
import com.ing.brokerage.repository.CustomerRepository;
import com.ing.brokerage.service.CustomerService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@AllArgsConstructor
public class DataInitializer {
    private final CustomerRepository customerRepository;
    private final AssetRepository assetRepository;
    private final CustomerService customerService;

    @PostConstruct
    public void init() {
        // create customers
        if (!customerRepository.existsById("cust1")) customerService.createCustomer("cust1","pass1","Ayşe");
        if (!customerRepository.existsById("cust2")) customerService.createCustomer("cust2","pass2","Ali");

        // TRY balances using BigDecimal major units
        Asset a1 = new Asset("cust1","TRY", 100000L, 100000L);
        a1.setTryAmount(new BigDecimal("10000.00")); // 10,000.00 TRY
        assetRepository.save(a1);

        Asset a2 = new Asset("cust1","AAPL", 100L, 100L);
        assetRepository.save(a2);

        Asset b1 = new Asset("cust2","TRY", 5000L, 5000L);
        b1.setTryAmount(new BigDecimal("5000.00"));
        assetRepository.save(b1);
    }
}
