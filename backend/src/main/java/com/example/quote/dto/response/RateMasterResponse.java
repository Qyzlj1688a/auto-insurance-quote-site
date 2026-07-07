package com.example.quote.dto.response;

import com.example.quote.entity.RateMaster;

import java.math.BigDecimal;

/**
 * Rate master response.
 */
public class RateMasterResponse {

    private String category;
    private String itemCode;
    private String itemName;
    private BigDecimal rate;
    private Integer amount;
    private Boolean active;

    public static RateMasterResponse from(RateMaster entity) {
        RateMasterResponse response = new RateMasterResponse();
        response.setCategory(entity.getCategory());
        response.setItemCode(entity.getItemCode());
        response.setItemName(entity.getItemName());
        response.setRate(entity.getRate());
        response.setAmount(entity.getAmount());
        response.setActive(entity.getActive());
        return response;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
