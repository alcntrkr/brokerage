package com.ing.brokerage.service;

import com.ing.brokerage.entity.Asset;
import com.ing.brokerage.entity.Order;
import com.ing.brokerage.entity.OrderSide;
import com.ing.brokerage.entity.OrderStatus;
import com.ing.brokerage.repository.AssetRepository;
import com.ing.brokerage.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepo;
    private final AssetRepository assetRepo;

    @Transactional
    public Order createOrder(Order order) {
        String customerId = order.getCustomerId();
        if (order.getSide() == OrderSide.BUY) {
            // required amount = size * price
            BigDecimal required = order.getPrice().multiply(BigDecimal.valueOf(order.getSize()));
            Asset tryAsset = assetRepo.findByCustomerIdAndAssetName(customerId, "TRY")
                    .orElseThrow(() -> new IllegalArgumentException("Customer has no TRY asset or insufficient funds"));
            BigDecimal available = tryAsset.getTryAmount() == null ? BigDecimal.ZERO : tryAsset.getTryAmount();
            if (available.compareTo(required) < 0) throw new IllegalArgumentException("Insufficient TRY usable amount");
            // reserve: subtract from tryAmount (usable)
            tryAsset.setTryAmount(available.subtract(required));
            tryAsset.setUsableSize(tryAsset.getTryAmount().multiply(new BigDecimal(100)).longValue());
            assetRepo.save(tryAsset);
        } else {
            Asset asset = assetRepo.findByCustomerIdAndAssetName(customerId, order.getAssetName())
                    .orElseThrow(() -> new IllegalArgumentException("Customer doesn't have asset to sell"));
            if (asset.getUsableSize() < order.getSize()) throw new IllegalArgumentException("Insufficient asset usable size to sell");
            asset.setUsableSize(asset.getUsableSize() - order.getSize());
            assetRepo.save(asset);
        }
        order.setStatus(OrderStatus.PENDING);
        order.setCreateDate(Instant.now());
        return orderRepo.save(order);
    }

    public List<Order> listOrders(String customerId, Instant from, Instant to) {
        if (from == null || to == null) return orderRepo.findByCustomerId(customerId);
        return orderRepo.findByCustomerIdAndCreateDateBetween(customerId, from, to);
    }

    public Optional<Order> getOrder(Long id) { return orderRepo.findById(id); }

    @Transactional
    public void cancelOrder(Long id) {
        Order o = orderRepo.findById(id).orElseThrow();
        if (o.getStatus() != OrderStatus.PENDING) throw new IllegalStateException("Only PENDING orders can be canceled");
        if (o.getSide() == OrderSide.BUY) {
            BigDecimal reserved = o.getPrice().multiply(BigDecimal.valueOf(o.getSize()));
            Asset tryAsset = assetRepo.findByCustomerIdAndAssetName(o.getCustomerId(), "TRY").orElseThrow();
            BigDecimal prev = tryAsset.getTryAmount() == null ? BigDecimal.ZERO : tryAsset.getTryAmount();
            tryAsset.setTryAmount(prev.add(reserved));
            tryAsset.setUsableSize(tryAsset.getTryAmount().multiply(new BigDecimal(100)).longValue());
            assetRepo.save(tryAsset);
        } else {
            Asset asset = assetRepo.findByCustomerIdAndAssetName(o.getCustomerId(), o.getAssetName()).orElseThrow();
            asset.setUsableSize(asset.getUsableSize() + o.getSize());
            assetRepo.save(asset);
        }
        o.setStatus(OrderStatus.CANCELED);
        orderRepo.save(o);
    }

    @Transactional
    public Order matchOrder(Long id) {
        Order o = orderRepo.findById(id).orElseThrow();
        if (o.getStatus() != OrderStatus.PENDING) throw new IllegalStateException("Only PENDING orders can be matched");
        if (o.getSide() == OrderSide.BUY) {
            BigDecimal total = o.getPrice().multiply(BigDecimal.valueOf(o.getSize()));
            Asset tryAsset = assetRepo.findByCustomerIdAndAssetName(o.getCustomerId(), "TRY").orElseThrow();
            // tryAmount was already reduced when creating order; now permanently deduct by doing nothing further to tryAmount
            Asset bought = assetRepo.findByCustomerIdAndAssetName(o.getCustomerId(), o.getAssetName())
                    .orElseGet(() -> assetRepo.save(new Asset(o.getCustomerId(), o.getAssetName(), 0L, 0L)));
            bought.setSize(bought.getSize() + o.getSize());
            bought.setUsableSize(bought.getUsableSize() + o.getSize());
            assetRepo.save(bought);
        } else {
            Asset asset = assetRepo.findByCustomerIdAndAssetName(o.getCustomerId(), o.getAssetName()).orElseThrow();
            asset.setSize(asset.getSize() - o.getSize());
            // increase TRY balance by proceeds
            Asset tryAsset = assetRepo.findByCustomerIdAndAssetName(o.getCustomerId(), "TRY")
                    .orElseGet(() -> assetRepo.save(new Asset(o.getCustomerId(), "TRY", 0L, 0L)));
            BigDecimal proceeds = o.getPrice().multiply(BigDecimal.valueOf(o.getSize()));
            BigDecimal prev = tryAsset.getTryAmount() == null ? BigDecimal.ZERO : tryAsset.getTryAmount();
            tryAsset.setTryAmount(prev.add(proceeds));
            tryAsset.setUsableSize(tryAsset.getTryAmount().multiply(new java.math.BigDecimal(100)).longValue());
            assetRepo.save(asset);
            assetRepo.save(tryAsset);
        }
        o.setStatus(OrderStatus.MATCHED);
        return orderRepo.save(o);
    }
}
