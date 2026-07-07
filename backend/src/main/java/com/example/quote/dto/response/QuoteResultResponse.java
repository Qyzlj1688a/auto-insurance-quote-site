package com.example.quote.dto.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * Quote result response DTO.
 */
public class QuoteResultResponse {

    private String quoteNo;
    private Integer annualPremium;
    private Integer monthlyPremium;
    private List<BreakdownResponse> breakdowns;
    private String createdAt; // ISO-8601 format
    private Integer driverAge;
    private String licenseColor;
    private String usageType;
    private Integer annualMileage;
    private String driverRange;
    private Boolean hasCurrentInsurance;
    private Integer grade;
    private Integer accidentTerm;
    private String maker;
    private String carName;
    private String firstRegistrationYearMonth;
    private String vehicleType;
    private Boolean vehicleInsurance;
    private String propertyDamageLimit;
    private String personalInjuryAmount;
    private Boolean lawyerOption;
    private Boolean roadService;

    public static class BreakdownResponse {
        private String itemCode;
        private String itemName;
        private BigDecimal rate;
        private Integer amount;
        private Integer displayOrder;

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

        public Integer getDisplayOrder() {
            return displayOrder;
        }

        public void setDisplayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
        }
    }

    // Getters and Setters
    public String getQuoteNo() {
        return quoteNo;
    }

    public void setQuoteNo(String quoteNo) {
        this.quoteNo = quoteNo;
    }

    public Integer getAnnualPremium() {
        return annualPremium;
    }

    public void setAnnualPremium(Integer annualPremium) {
        this.annualPremium = annualPremium;
    }

    public Integer getMonthlyPremium() {
        return monthlyPremium;
    }

    public void setMonthlyPremium(Integer monthlyPremium) {
        this.monthlyPremium = monthlyPremium;
    }

    public List<BreakdownResponse> getBreakdowns() {
        return breakdowns;
    }

    public void setBreakdowns(List<BreakdownResponse> breakdowns) {
        this.breakdowns = breakdowns;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getDriverAge() {
        return driverAge;
    }

    public void setDriverAge(Integer driverAge) {
        this.driverAge = driverAge;
    }

    public String getLicenseColor() {
        return licenseColor;
    }

    public void setLicenseColor(String licenseColor) {
        this.licenseColor = licenseColor;
    }

    public String getUsageType() {
        return usageType;
    }

    public void setUsageType(String usageType) {
        this.usageType = usageType;
    }

    public Integer getAnnualMileage() {
        return annualMileage;
    }

    public void setAnnualMileage(Integer annualMileage) {
        this.annualMileage = annualMileage;
    }

    public String getDriverRange() {
        return driverRange;
    }

    public void setDriverRange(String driverRange) {
        this.driverRange = driverRange;
    }

    public Boolean getHasCurrentInsurance() {
        return hasCurrentInsurance;
    }

    public void setHasCurrentInsurance(Boolean hasCurrentInsurance) {
        this.hasCurrentInsurance = hasCurrentInsurance;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public Integer getAccidentTerm() {
        return accidentTerm;
    }

    public void setAccidentTerm(Integer accidentTerm) {
        this.accidentTerm = accidentTerm;
    }

    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getFirstRegistrationYearMonth() {
        return firstRegistrationYearMonth;
    }

    public void setFirstRegistrationYearMonth(String firstRegistrationYearMonth) {
        this.firstRegistrationYearMonth = firstRegistrationYearMonth;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Boolean getVehicleInsurance() {
        return vehicleInsurance;
    }

    public void setVehicleInsurance(Boolean vehicleInsurance) {
        this.vehicleInsurance = vehicleInsurance;
    }

    public String getPropertyDamageLimit() {
        return propertyDamageLimit;
    }

    public void setPropertyDamageLimit(String propertyDamageLimit) {
        this.propertyDamageLimit = propertyDamageLimit;
    }

    public String getPersonalInjuryAmount() {
        return personalInjuryAmount;
    }

    public void setPersonalInjuryAmount(String personalInjuryAmount) {
        this.personalInjuryAmount = personalInjuryAmount;
    }

    public Boolean getLawyerOption() {
        return lawyerOption;
    }

    public void setLawyerOption(Boolean lawyerOption) {
        this.lawyerOption = lawyerOption;
    }

    public Boolean getRoadService() {
        return roadService;
    }

    public void setRoadService(Boolean roadService) {
        this.roadService = roadService;
    }
}

