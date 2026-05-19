package com.consignment.dto;

import java.math.BigDecimal;

public class IdentifyRequest {
    private String itemId;
    private Boolean pass;
    private BigDecimal finalPrice;
    private Integer consignmentDays;
    private String remark;

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public Boolean getPass() { return pass; }
    public void setPass(Boolean pass) { this.pass = pass; }
    public BigDecimal getFinalPrice() { return finalPrice; }
    public void setFinalPrice(BigDecimal finalPrice) { this.finalPrice = finalPrice; }
    public Integer getConsignmentDays() { return consignmentDays; }
    public void setConsignmentDays(Integer consignmentDays) { this.consignmentDays = consignmentDays; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
