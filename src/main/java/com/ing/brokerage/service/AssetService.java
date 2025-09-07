package com.ing.brokerage.service;

import com.ing.brokerage.entity.Asset;
import com.ing.brokerage.repository.AssetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class AssetService {
    private final AssetRepository repo;

    public AssetService(AssetRepository repo) { this.repo = repo; }

    public List<Asset> listAssets(String customerId) {
        return repo.findByCustomerId(customerId);
    }

    @Transactional
    public Asset getOrCreate(String customerId, String assetName) {
        return repo.findByCustomerIdAndAssetName(customerId, assetName)
                .orElseGet(() -> repo.save(new Asset(customerId, assetName, 0L, 0L)));
    }

    @Transactional
    public void adjustUsableForTry(String customerId, BigDecimal deltaAmount) {
        Asset a = getOrCreate(customerId, "TRY");
        BigDecimal prev = a.getTryAmount() == null ? BigDecimal.ZERO : a.getTryAmount();
        BigDecimal next = prev.add(deltaAmount);
        a.setTryAmount(next);
        // usableSize maintain as long representing minor units (e.g., cents) if needed; for demo use long of major*100
        long minor = next.multiply(new BigDecimal(100)).longValue();
        a.setUsableSize(minor);
        a.setSize(minor);
        repo.save(a);
    }

    @Transactional
    public void reserveTry(String customerId, BigDecimal amount) {
        Asset a = getOrCreate(customerId, "TRY");
        BigDecimal usable = a.getTryAmount() == null ? BigDecimal.ZERO : a.getTryAmount();
        if (usable.compareTo(amount) < 0) throw new IllegalArgumentException("Insufficient TRY amount");
        a.setTryAmount(usable.subtract(amount));
        long minor = a.getTryAmount().multiply(new BigDecimal(100)).longValue();
        a.setUsableSize(minor);
        a.setSize(minor);
        repo.save(a);
    }
}
