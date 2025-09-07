package com.ing.brokerage;

import com.ing.brokerage.config.DataInitializer;
import com.ing.brokerage.entity.Asset;
import com.ing.brokerage.entity.Order;
import com.ing.brokerage.entity.OrderSide;
import com.ing.brokerage.entity.OrderStatus;
import com.ing.brokerage.repository.AssetRepository;
import com.ing.brokerage.repository.OrderRepository;
import com.ing.brokerage.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.crypto.Data;
import java.math.BigDecimal;

@DataJpaTest
public class OrderServiceUnitTests {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    AssetRepository assetRepository;

    @Test
    public void testCreateBuyOrderAndCancel() {
        // prepare TRY asset
        Asset a = new Asset("custZ", "TRY", 0L, 0L);
        a.setTryAmount(new BigDecimal("1000.00"));
        assetRepository.save(a);

        OrderService svc = new OrderService(orderRepository, assetRepository);
        Order o = new Order(
                "custZ", "AAPL", OrderSide.BUY,
                1L, new BigDecimal("100.00"), OrderStatus.PENDING);
        var saved = svc.createOrder(o);
        Assertions.assertEquals(OrderStatus.PENDING, saved.getStatus());
        svc.cancelOrder(saved.getId());
        var o2 = orderRepository.findById(saved.getId()).get();
        Assertions.assertEquals(OrderStatus.CANCELED, o2.getStatus());
    }
}
