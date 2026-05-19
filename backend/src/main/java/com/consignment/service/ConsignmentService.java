package com.consignment.service;

import com.consignment.common.ItemStatus;
import com.consignment.dto.IdentifyRequest;
import com.consignment.dto.OrderRequest;
import com.consignment.dto.SubmitItemRequest;
import com.consignment.entity.ConsignmentItem;
import com.consignment.entity.ReturnTask;
import com.consignment.entity.SettlementRecord;
import com.consignment.repository.ConsignmentItemRepository;
import com.consignment.repository.ReturnTaskRepository;
import com.consignment.repository.SettlementRecordRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ConsignmentService {
    private final ConsignmentItemRepository itemRepository;
    private final SettlementRecordRepository settlementRepository;
    private final ReturnTaskRepository returnTaskRepository;

    @Value("${consignment.commission-rate:0.05}")
    private BigDecimal commissionRate;

    @Value("${consignment.default-consignment-days:30}")
    private Integer defaultConsignmentDays;

    public ConsignmentService(ConsignmentItemRepository itemRepository,
                           SettlementRecordRepository settlementRepository,
                           ReturnTaskRepository returnTaskRepository) {
        this.itemRepository = itemRepository;
        this.settlementRepository = settlementRepository;
        this.returnTaskRepository = returnTaskRepository;
    }

    public ConsignmentItem submitItem(SubmitItemRequest request) {
        List<ItemStatus> lockedStatuses = Arrays.asList(
                ItemStatus.PENDING_IDENTIFICATION,
                ItemStatus.ON_SALE,
                ItemStatus.PENDING_PAYMENT
        );
        if (itemRepository.existsByUserIdAndItemNameAndStatusIn(
                request.getUserId(), request.getItemName(), lockedStatuses)) {
            throw new RuntimeException("该商品已在寄售中，不能重复提交");
        }

        ConsignmentItem item = new ConsignmentItem();
        item.setId(UUID.randomUUID().toString().replace("-", ""));
        item.setUserId(request.getUserId());
        item.setUserName(request.getUserName());
        item.setItemName(request.getItemName());
        item.setDescription(request.getDescription());
        item.setCategory(request.getCategory());
        item.setExpectedPrice(request.getExpectedPrice());
        item.setStatus(ItemStatus.PENDING_IDENTIFICATION);
        item.setSubmitTime(LocalDateTime.now());
        item.setCommissionRate(commissionRate);
        return itemRepository.save(item);
    }

    public ConsignmentItem identifyItem(IdentifyRequest request) {
        ConsignmentItem item = itemRepository.findById(request.getItemId());
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!ItemStatus.PENDING_IDENTIFICATION.equals(item.getStatus())) {
            throw new RuntimeException("商品状态不正确");
        }

        item.setIdentificationTime(LocalDateTime.now());
        item.setIdentificationRemark(request.getRemark());

        if (request.getPass()) {
            BigDecimal finalPrice = request.getFinalPrice() != null
                    ? request.getFinalPrice()
                    : item.getExpectedPrice();
            Integer consignmentDays = request.getConsignmentDays() != null
                    ? request.getConsignmentDays()
                    : defaultConsignmentDays;

            item.setFinalPrice(finalPrice);
            item.setConsignmentDays(consignmentDays);
            item.setCommission(finalPrice.multiply(commissionRate).setScale(2, RoundingMode.HALF_UP));
            item.setOnSaleTime(LocalDateTime.now());
            item.setExpireTime(LocalDateTime.now().plusDays(consignmentDays));
            item.setStatus(ItemStatus.ON_SALE);
        } else {
            item.setStatus(ItemStatus.IDENTIFICATION_REJECTED);
        }

        return itemRepository.save(item);
    }

    public ConsignmentItem placeOrder(OrderRequest request) {
        ConsignmentItem item = itemRepository.findById(request.getItemId());
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!ItemStatus.ON_SALE.equals(item.getStatus())) {
            throw new RuntimeException("商品不可购买");
        }

        item.setBuyerId(request.getBuyerId());
        item.setBuyerName(request.getBuyerName());
        item.setOrderTime(LocalDateTime.now());
        item.setStatus(ItemStatus.PENDING_PAYMENT);
        return itemRepository.save(item);
    }

    public ConsignmentItem payOrder(String itemId) {
        ConsignmentItem item = itemRepository.findById(itemId);
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!ItemStatus.PENDING_PAYMENT.equals(item.getStatus())) {
            throw new RuntimeException("商品状态不正确");
        }

        item.setPayTime(LocalDateTime.now());
        item.setStatus(ItemStatus.SOLD);
        itemRepository.save(item);

        createSettlementRecord(item);
        return item;
    }

    private void createSettlementRecord(ConsignmentItem item) {
        SettlementRecord record = new SettlementRecord();
        record.setId(UUID.randomUUID().toString().replace("-", ""));
        record.setItemId(item.getId());
        record.setItemName(item.getItemName());
        record.setUserId(item.getUserId());
        record.setUserName(item.getUserName());
        record.setSalePrice(item.getFinalPrice());
        record.setCommissionRate(item.getCommissionRate());
        record.setCommissionAmount(item.getCommission());
        record.setSettleAmount(item.getFinalPrice().subtract(item.getCommission()));
        record.setSettleTime(LocalDateTime.now());
        settlementRepository.save(record);
    }

    public ConsignmentItem releaseItem(String itemId) {
        ConsignmentItem item = itemRepository.findById(itemId);
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!ItemStatus.PENDING_PAYMENT.equals(item.getStatus())) {
            throw new RuntimeException("商品状态不正确");
        }

        item.setBuyerId(null);
        item.setBuyerName(null);
        item.setOrderTime(null);
        item.setStatus(ItemStatus.ON_SALE);
        return itemRepository.save(item);
    }

    public void expireItem(ConsignmentItem item) {
        item.setStatus(ItemStatus.EXPIRED);
        itemRepository.save(item);

        if (!returnTaskRepository.existsByItemId(item.getId())) {
            ReturnTask task = new ReturnTask();
            task.setId(UUID.randomUUID().toString().replace("-", ""));
            task.setItemId(item.getId());
            task.setItemName(item.getItemName());
            task.setUserId(item.getUserId());
            task.setUserName(item.getUserName());
            task.setCreateTime(LocalDateTime.now());
            task.setStatus("PENDING");
            returnTaskRepository.save(task);
        }
    }

    public List<ConsignmentItem> getAllItems() {
        return itemRepository.findAll();
    }

    public List<ConsignmentItem> getItemsByStatus(ItemStatus status) {
        return itemRepository.findByStatus(status);
    }

    public List<ConsignmentItem> getItemsByUserId(String userId) {
        return itemRepository.findByUserId(userId);
    }

    public List<SettlementRecord> getSettlementRecords() {
        return settlementRepository.findAll();
    }

    public List<SettlementRecord> getSettlementRecordsByUserId(String userId) {
        return settlementRepository.findByUserId(userId);
    }

    public List<ReturnTask> getReturnTasks() {
        return returnTaskRepository.findAll();
    }

    public ConsignmentItem getItemById(String id) {
        return itemRepository.findById(id);
    }

    public ReturnTask completeReturnTask(String taskId) {
        ReturnTask task = returnTaskRepository.findById(taskId);
        if (task == null) {
            throw new RuntimeException("退回任务不存在");
        }
        if (!"PENDING".equals(task.getStatus())) {
            throw new RuntimeException("任务已处理");
        }

        ConsignmentItem item = itemRepository.findById(task.getItemId());
        if (item != null && ItemStatus.EXPIRED.equals(item.getStatus())) {
            item.setStatus(ItemStatus.RETURNED);
            itemRepository.save(item);
        }

        task.setStatus("COMPLETED");
        return returnTaskRepository.save(task);
    }
}
