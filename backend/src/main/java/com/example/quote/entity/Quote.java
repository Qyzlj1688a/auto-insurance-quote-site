package com.example.quote.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Quote entity.
 */
@Entity
@Table(name = "quotes")
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quote_no", nullable = false, length = 20)
    private String quoteNo;

    @Column(name = "driver_age", nullable = false)
    private Integer driverAge;

    @Column(name = "license_color", nullable = false, length = 20)
    private String licenseColor;

    @Column(name = "usage_type", nullable = false, length = 20)
    private String usageType;

    @Column(name = "annual_mileage", nullable = false)
    private Integer annualMileage;

    @Column(name = "driver_range", nullable = false, length = 20)
    private String driverRange;

    @Column(name = "has_current_insurance", nullable = false)
    private Boolean hasCurrentInsurance;

    private Integer grade;

    @Column(name = "accident_term")
    private Integer accidentTerm;

    @Column(nullable = false, length = 50)
    private String maker;

    @Column(name = "car_name", nullable = false, length = 50)
    private String carName;

    @Column(name = "first_registration_ym", nullable = false, length = 7)
    private String firstRegistrationYm;

    @Column(name = "vehicle_type", nullable = false, length = 20)
    private String vehicleType;

    @Column(name = "vehicle_insurance", nullable = false)
    private Boolean vehicleInsurance;

    @Column(name = "property_damage_limit", nullable = false, length = 20)
    private String propertyDamageLimit;

    @Column(name = "personal_injury_amount", nullable = false, length = 20)
    private String personalInjuryAmount;

    @Column(name = "lawyer_option", nullable = false)
    private Boolean lawyerOption;

    @Column(name = "road_service", nullable = false)
    private Boolean roadService;

    @Column(name = "annual_premium", nullable = false)
    private Integer annualPremium;

    @Column(name = "monthly_premium", nullable = false)
    private Integer monthlyPremium;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuoteNo() {
        return quoteNo;
    }

    public void setQuoteNo(String quoteNo) {
        this.quoteNo = quoteNo;
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

    public String getFirstRegistrationYm() {
        return firstRegistrationYm;
    }

    public void setFirstRegistrationYm(String firstRegistrationYm) {
        this.firstRegistrationYm = firstRegistrationYm;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
