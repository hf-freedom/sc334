package com.consignment.service;

import com.consignment.common.ItemStatus;
import com.consignment.entity.ConsignmentItem;
import com.consignment.repository.ConsignmentItemRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduledTaskService {
    private final ConsignmentItemRepository itemRepository;
    private final ConsignmentService consignmentService;

    @Value("${consignment.payment-timeout-minutes:30}")
    private Integer paymentTimeoutMinutes;

    public ScheduledTaskService(ConsignmentItemRepository itemRepository,
                                ConsignmentService consignmentService) {
        this.itemRepository = itemRepository;
        this.consignmentService = consignmentService;
    }

    @Scheduled(cron = "0 * * * * ?")
    public void scanPendingPayment() {
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(paymentTimeoutMinutes);
        List<ConsignmentItem> expiredItems = itemRepository.findPendingPaymentExpired(expireTime);
        for (ConsignmentItem item : expiredItems) {
            try {
                consignmentService.releaseItem(item.getId());
                System.out.println("定时任务：商品[" + item.getItemName() + "]支付超时，已自动释放");
            } catch (Exception e) {
                System.err.println("释放商品失败: " + item.getId() + ", " + e.getMessage());
            }
        }
    }

    @Scheduled(cron = "0 * * * * ?")
    public void scanExpiredItems() {
        LocalDateTime now = LocalDateTime.now();
        List<ConsignmentItem> expiredItems = itemRepository.findExpiredItems(now);
        for (ConsignmentItem item : expiredItems) {
            try {
                consignmentService.expireItem(item);
                System.out.println("定时任务：商品[" + item.getItemName() + "]寄售到期，已自动下架并生成退回任务");
            } catch (Exception e) {
                System.err.println("下架商品失败: " + item.getId() + ", " + e.getMessage());
            }
        }
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void scanPendingIdentification() {
        List<ConsignmentItem> items = itemRepository.findByStatus(ItemStatus.PENDING_IDENTIFICATION);
        if (!items.isEmpty()) {
            System.out.println("定时任务：当前有待鉴定商品 " + items.size() + " 件");
        }
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void scanExpiringItems() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.plusDays(1);
        LocalDateTime endTime = now.plusDays(3);
        List<ConsignmentItem> expiringItems = itemRepository.findExpiringItems(startTime, endTime);
        if (!expiringItems.isEmpty()) {
            System.out.println("定时任务：以下商品即将到期：");
            for (ConsignmentItem item : expiringItems) {
                System.out.println("  - " + item.getItemName() + " (到期时间: " + item.getExpireTime() + ")");
            }
        }
    }
}
