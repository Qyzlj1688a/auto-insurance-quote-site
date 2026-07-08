package com.example.quote.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 見積作成要求パラメータのDTOクラス。
 */
public class QuoteCreateRequest {

    @NotNull(message = "年齢は必須項目です。")
    @Min(value = 18, message = "年齢は18〜100歳の間で入力してください。")
    @Max(value = 100, message = "年齢は18〜100歳の間で入力してください。")
    private Integer driverAge;

    @NotBlank(message = "免許証の色は必須項目です。")
    @Pattern(regexp = "GOLD|BLUE|GREEN", message = "免許証の色が正しくありません。")
    private String licenseColor;

    @NotBlank(message = "使用目的は必須項目です。")
    @Pattern(regexp = "PRIVATE|COMMUTE|BUSINESS", message = "使用目的が正しくありません。")
    private String usageType;

    @NotNull(message = "年間走行距離は必須項目です。")
    @Min(value = 0, message = "年間走行距離は0〜30000kmの間で入力してください。")
    @Max(value = 30000, message = "年間走行距離は0〜30000kmの間为入力してください。")
    private Integer annualMileage;

    @NotBlank(message = "運転者範囲は必须項目です。")
    @Pattern(regexp = "SELF|COUPLE|FAMILY|ANYONE", message = "運転者範囲が正しくありません。")
    private String driverRange;

    @NotNull(message = "現在加入有無は必須項目です。")
    private Boolean hasCurrentInsurance;

    @Min(value = 1, message = "等級は1〜20の間で入力してください。")
    @Max(value = 20, message = "等級は1〜20の間で入力してください。")
    private Integer grade;

    @Min(value = 0, message = "事故有係数期間は0〜6年の間で入力してください。")
    @Max(value = 6, message = "事故有係数期間は0〜6年の間で入力してください。")
    private Integer accidentTerm;

    @NotBlank(message = "メーカーは必須項目です。")
    @Size(max = 50, message = "メーカーは50文字以内で入力してください。")
    private String maker;

    @NotBlank(message = "車名は必須項目です。")
    @Size(max = 50, message = "車名は50文字以内で输入してください。")
    private String carName;

    @NotBlank(message = "初度登録年月は必須項目です。")
    @Pattern(regexp = "^[0-9]{4}-(0[1-9]|1[0-2])$", message = "初度登録年月はYYYY-MM形式で入力してください。")
    private String firstRegistrationYearMonth;

    @NotBlank(message = "車両タイプは必須項目です。")
    @Pattern(regexp = "COMPACT|SEDAN|MINIVAN|SUV|KEI", message = "車両タイプが正しくありません。")
    private String vehicleType;

    @NotNull(message = "車両保険の有無は必須項目です。")
    private Boolean vehicleInsurance;

    @NotBlank(message = "対物補償は必須項目です。")
    @Pattern(regexp = "UNLIMITED|THIRTY_MILLION", message = "対物補償が正しくありません。")
    private String propertyDamageLimit;

    @NotBlank(message = "人身傷害は必須項目です。")
    @Pattern(regexp = "THIRTY_MILLION|FIFTY_MILLION|UNLIMITED", message = "人身傷害が正しくありません。")
    private String personalInjuryAmount;

    @NotNull(message = "弁護士特約は必須項目です。")
    private Boolean lawyerOption;

    @NotNull(message = "ロードサービスは必須項目です。")
    private Boolean roadService;

    // ゲッターとセッター
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

