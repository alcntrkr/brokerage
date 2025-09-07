package com.ing.brokerage;

import com.ing.brokerage.repository.AssetRepository;
import com.ing.brokerage.service.AssetService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

@DataJpaTest
public class AssetServiceUnitTests {
    @Autowired AssetRepository assetRepository;

    @Test
    public void testGetOrCreateAndAdjustTry() {
        AssetService svc = new AssetService(assetRepository);
        var a = svc.getOrCreate("custA","TRY");
        Assertions.assertNotNull(a);
        svc.adjustUsableForTry("custA", new BigDecimal("123.45"));
        var b = assetRepository.findByCustomerIdAndAssetName("custA","TRY").get();
        Assertions.assertEquals(new BigDecimal("123.45"), b.getTryAmount());
    }
}
