package com.consignment.repository;

import com.consignment.common.ItemStatus;
import com.consignment.entity.ConsignmentItem;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class ConsignmentItemRepository {
    private final Map<String, ConsignmentItem> itemMap = new ConcurrentHashMap<>();

    public ConsignmentItem save(ConsignmentItem item) {
        itemMap.put(item.getId(), item);
        return item;
    }

    public ConsignmentItem findById(String id) {
        return itemMap.get(id);
    }

    public List<ConsignmentItem> findAll() {
        return new ArrayList<>(itemMap.values());
    }

    public List<ConsignmentItem> findByStatus(ItemStatus status) {
        return itemMap.values().stream()
                .filter(item -> status.equals(item.getStatus()))
                .collect(Collectors.toList());
    }

    public List<ConsignmentItem> findByUserId(String userId) {
        return itemMap.values().stream()
                .filter(item -> userId.equals(item.getUserId()))
                .collect(Collectors.toList());
    }

    public boolean existsByUserIdAndItemNameAndStatusIn(String userId, String itemName, List<ItemStatus> statuses) {
        return itemMap.values().stream()
                .anyMatch(item -> userId.equals(item.getUserId())
                        && itemName.equals(item.getItemName())
                        && statuses.contains(item.getStatus()));
    }

    public List<ConsignmentItem> findPendingPaymentExpired(LocalDateTime expireTime) {
        return itemMap.values().stream()
                .filter(item -> ItemStatus.PENDING_PAYMENT.equals(item.getStatus())
                        && item.getOrderTime() != null
                        && item.getOrderTime().isBefore(expireTime))
                .collect(Collectors.toList());
    }

    public List<ConsignmentItem> findExpiredItems(LocalDateTime now) {
        return itemMap.values().stream()
                .filter(item -> ItemStatus.ON_SALE.equals(item.getStatus())
                        && item.getExpireTime() != null
                        && item.getExpireTime().isBefore(now))
                .collect(Collectors.toList());
    }

    public List<ConsignmentItem> findExpiringItems(LocalDateTime startTime, LocalDateTime endTime) {
        return itemMap.values().stream()
                .filter(item -> ItemStatus.ON_SALE.equals(item.getStatus())
                        && item.getExpireTime() != null
                        && !item.getExpireTime().isBefore(startTime)
                        && item.getExpireTime().isBefore(endTime))
                .collect(Collectors.toList());
    }
}
