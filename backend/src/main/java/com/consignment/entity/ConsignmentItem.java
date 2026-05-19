package com.consignment.entity;

import com.consignment.common.ItemStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ConsignmentItem {
    private String id;
    private String userId;
    private String userName;
    private String itemName;
    private String description;
    private String category;
    private BigDecimal expectedPrice;
    private BigDecimal finalPrice;
    private BigDecimal commission;
    private BigDecimal commissionRate;
    private Integer consignmentDays;
    private LocalDateTime submitTime;
    private LocalDateTime identificationTime;
    private LocalDateTime onSaleTime;
    private LocalDateTime expireTime;
    private LocalDateTime orderTime;
    private LocalDateTime payTime;
    private String buyerId;
    private String buyerName;
    private ItemStatus status;
    private String identificationRemark;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getExpectedPrice() { return expectedPrice; }
    public void setExpectedPrice(BigDecimal expectedPrice) { this.expectedPrice = expectedPrice; }
    public BigDecimal getFinalPrice() { return finalPrice; }
    public void setFinalPrice(BigDecimal finalPrice) { this.finalPrice = finalPrice; }
    public BigDecimal getCommission() { return commission; }
    public void setCommission(BigDecimal commission) { this.commission = commission; }
    public BigDecimal getCommissionRate() { return commissionRate; }
    public void setCommissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; }
    public Integer getConsignmentDays() { return consignmentDays; }
    public void setConsignmentDays(Integer consignmentDays) { this.consignmentDays = consignmentDays; }
    public LocalDateTime getSubmitTime() { return submitTime; }
    public void setSubmitTime(LocalDateTime submitTime) { this.submitTime = submitTime; }
    public LocalDateTime getIdentificationTime() { return identificationTime; }
    public void setIdentificationTime(LocalDateTime identificationTime) { this.identificationTime = identificationTime; }
    public LocalDateTime getOnSaleTime() { return onSaleTime; }
    public void setOnSaleTime(LocalDateTime onSaleTime) { this.onSaleTime = onSaleTime; }
    public LocalDateTime getExpireTime() { return expireTime; }
    public void setExpireTime(LocalDateTime expireTime) { this.expireTime = expireTime; }
    public LocalDateTime getOrderTime() { return orderTime; }
    public void setOrderTime(LocalDateTime orderTime) { this.orderTime = orderTime; }
    public LocalDateTime getPayTime() { return payTime; }
    public void setPayTime(LocalDateTime payTime) { this.payTime = payTime; }
    public String getBuyerId() { return buyerId; }
    public void setBuyerId(String buyerId) { this.buyerId = buyerId; }
    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
    public ItemStatus getStatus() { return status; }
    public void setStatus(ItemStatus status) { this.status = status; }
    public String getIdentificationRemark() { return identificationRemark; }
    public void setIdentificationRemark(String identificationRemark) { this.identificationRemark = identificationRemark; }
}
