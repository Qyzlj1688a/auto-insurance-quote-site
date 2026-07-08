package com.example.quote.service.impl;

import com.example.quote.dto.QuoteCalculationResult;
import com.example.quote.dto.request.QuoteCreateRequest;
import com.example.quote.entity.QuoteBreakdown;
import com.example.quote.entity.RateMaster;
import com.example.quote.exception.BusinessException;
import com.example.quote.service.PremiumCalculator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 保険料計算エンジン実装クラス。
 */
@Component
public class PremiumCalculatorImpl implements PremiumCalculator {

    @Override
    public QuoteCalculationResult calculate(QuoteCreateRequest request, List<RateMaster> rates) {
        // 料率マスタをカテゴリおよびアイテムコードでグルーピングしてマッピング
        Map<String, Map<String, RateMaster>> lookup = new HashMap<>();
        for (RateMaster rm : rates) {
            lookup.computeIfAbsent(rm.getCategory(), k -> new HashMap<>()).put(rm.getItemCode(), rm);
        }

        List<QuoteBreakdown> breakdowns = new ArrayList<>();
        int displayOrder = 1;

        // 1. 基本保険料の取得
        RateMaster baseMaster = getRateMaster(lookup, "BASE_PREMIUM", "BASE");
        BigDecimal currentPremium = BigDecimal.valueOf(baseMaster.getAmount());
        breakdowns.add(createBreakdown(baseMaster, displayOrder++));

        // 2. 運転者年齢の料率適用
        String ageCode = getAgeCode(request.getDriverAge());
        RateMaster ageMaster = getRateMaster(lookup, "AGE", ageCode);
        currentPremium = currentPremium.multiply(ageMaster.getRate());
        breakdowns.add(createBreakdown(ageMaster, displayOrder++));

        // 3. 免許証色の料率適用
        RateMaster licenseMaster = getRateMaster(lookup, "LICENSE", request.getLicenseColor());
        currentPremium = currentPremium.multiply(licenseMaster.getRate());
        breakdowns.add(createBreakdown(licenseMaster, displayOrder++));

        // 4. 使用目的の料率適用
        RateMaster usageMaster = getRateMaster(lookup, "USAGE", request.getUsageType());
        currentPremium = currentPremium.multiply(usageMaster.getRate());
        breakdowns.add(createBreakdown(usageMaster, displayOrder++));

        // 5. 年間走行距離の料率適用
        String mileageCode = getMileageCode(request.getAnnualMileage());
        RateMaster mileageMaster = getRateMaster(lookup, "MILEAGE", mileageCode);
        currentPremium = currentPremium.multiply(mileageMaster.getRate());
        breakdowns.add(createBreakdown(mileageMaster, displayOrder++));

        // 6. 運転者範囲による料率の乗算
        RateMaster rangeMaster = getRateMaster(lookup, "DRIVER_RANGE", request.getDriverRange());
        currentPremium = currentPremium.multiply(rangeMaster.getRate());
        breakdowns.add(createBreakdown(rangeMaster, displayOrder++));

        // 7. 等級による料率の乗算（他社加入ありの場合のみ）
        if (Boolean.TRUE.equals(request.getHasCurrentInsurance())) {
            String gradeCode = getGradeCode(request.getGrade());
            RateMaster gradeMaster = getRateMaster(lookup, "GRADE", gradeCode);
            currentPremium = currentPremium.multiply(gradeMaster.getRate());
            breakdowns.add(createBreakdown(gradeMaster, displayOrder++));
        }

        // 8. 事故有係数適用期間による料率の乗算（他社加入ありの場合のみ）
        if (Boolean.TRUE.equals(request.getHasCurrentInsurance())) {
            String termCode = getAccidentTermCode(request.getAccidentTerm());
            RateMaster termMaster = getRateMaster(lookup, "ACCIDENT_TERM", termCode);
            currentPremium = currentPremium.multiply(termMaster.getRate());
            breakdowns.add(createBreakdown(termMaster, displayOrder++));
        }

        // 9. 車両タイプによる料率の乗算
        RateMaster typeMaster = getRateMaster(lookup, "VEHICLE_TYPE", request.getVehicleType());
        currentPremium = currentPremium.multiply(typeMaster.getRate());
        breakdowns.add(createBreakdown(typeMaster, displayOrder++));

        // 10. 車両保険付帯による加算額の適用
        RateMaster vehicleInsMaster = getRateMaster(lookup, "VEHICLE_INSURANCE", request.getVehicleInsurance().toString().toUpperCase());
        int vehicleInsAmount = vehicleInsMaster.getAmount();
        breakdowns.add(createBreakdown(vehicleInsMaster, displayOrder++));

        // 11. 対物賠償制限額による加算額の適用
        RateMaster propertyDamageMaster = getRateMaster(lookup, "PROPERTY_DAMAGE_LIMIT", request.getPropertyDamageLimit());
        int propertyDamageAmount = propertyDamageMaster.getAmount();
        breakdowns.add(createBreakdown(propertyDamageMaster, displayOrder++));

        // 12. 人身傷害補償額による加算額の適用
        RateMaster personalInjuryMaster = getRateMaster(lookup, "PERSONAL_INJURY_AMOUNT", request.getPersonalInjuryAmount());
        int personalInjuryAmount = personalInjuryMaster.getAmount();
        breakdowns.add(createBreakdown(personalInjuryMaster, displayOrder++));

        // 13. 弁護士費用特約による加算額の適用
        RateMaster lawyerMaster = getRateMaster(lookup, "LAWYER_OPTION", request.getLawyerOption().toString().toUpperCase());
        int lawyerAmount = lawyerMaster.getAmount();
        breakdowns.add(createBreakdown(lawyerMaster, displayOrder++));

        // 14. ロードサービスによる加算額の適用
        RateMaster roadServiceMaster = getRateMaster(lookup, "ROAD_SERVICE", request.getRoadService().toString().toUpperCase());
        int roadServiceAmount = roadServiceMaster.getAmount();
        breakdowns.add(createBreakdown(roadServiceMaster, displayOrder++));

        // 加算型補償・特約金額の合計を算出
        int totalAdditions = vehicleInsAmount + propertyDamageAmount + personalInjuryAmount + lawyerAmount + roadServiceAmount;

        // 年間保険料＝係数乗算後の保険料＋加算額の合計
        BigDecimal rawAnnualPremium = currentPremium.add(BigDecimal.valueOf(totalAdditions));

        // 10円未満を四捨五入（年間保険料）
        int annualPremium = rawAnnualPremium.setScale(-1, RoundingMode.HALF_UP).intValue();

        // 月額保険料＝年間保険料÷12、10円未満を四捨五入
        int monthlyPremium = BigDecimal.valueOf(annualPremium)
                .divide(BigDecimal.valueOf(12), 4, RoundingMode.HALF_UP)
                .setScale(-1, RoundingMode.HALF_UP)
                .intValue();

        return new QuoteCalculationResult(annualPremium, monthlyPremium, breakdowns);
    }

    private RateMaster getRateMaster(Map<String, Map<String, RateMaster>> lookup, String category, String itemCode) {
        Map<String, RateMaster> categoryMap = lookup.get(category);
        if (categoryMap == null) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "SYSTEM_ERROR", "料率マスタにカテゴリが存在しません: " + category);
        }
        RateMaster rm = categoryMap.get(itemCode);
        if (rm == null) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "SYSTEM_ERROR", "料率マスタにコードが存在しません: " + category + " -> " + itemCode);
        }
        return rm;
    }

    private QuoteBreakdown createBreakdown(RateMaster rateMaster, int displayOrder) {
        QuoteBreakdown breakdown = new QuoteBreakdown();
        breakdown.setItemCode(rateMaster.getCategory() + "_" + rateMaster.getItemCode());
        breakdown.setItemName(rateMaster.getItemName());
        breakdown.setRate(rateMaster.getRate());
        breakdown.setAmount(rateMaster.getAmount());
        breakdown.setDisplayOrder(displayOrder);
        return breakdown;
    }

    private String getAgeCode(int age) {
        if (age >= 18 && age <= 25) {
            return "AGE_18_25";
        } else if (age >= 26 && age <= 34) {
            return "AGE_26_34";
        } else if (age >= 35 && age <= 59) {
            return "AGE_35_59";
        } else if (age >= 60 && age <= 100) {
            return "AGE_60_OVER";
        }
        throw new BusinessException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "年齢は18〜100歳の間で入力してください。");
    }

    private String getMileageCode(int mileage) {
        if (mileage >= 0 && mileage <= 5000) {
            return "MILEAGE_0_5000";
        } else if (mileage > 5000 && mileage <= 10000) {
            return "MILEAGE_5001_10000";
        } else if (mileage > 10000 && mileage <= 30000) {
            return "MILEAGE_10001_OVER";
        }
        throw new BusinessException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "年間走行距離は0〜30000kmの間で入力してください。");
    }

    private String getGradeCode(Integer grade) {
        if (grade == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "等級は必須項目です。");
        }
        if (grade >= 1 && grade <= 5) {
            return "GRADE_1_5";
        } else if (grade >= 6 && grade <= 10) {
            return "GRADE_6_10";
        } else if (grade >= 11 && grade <= 15) {
            return "GRADE_11_15";
        } else if (grade >= 16 && grade <= 20) {
            return "GRADE_16_20";
        }
        throw new BusinessException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "等級は1〜20の間で入力してください。");
    }

    private String getAccidentTermCode(Integer term) {
        if (term == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "事故有係数期間は必須項目です。");
        }
        if (term == 0) {
            return "ACCIDENT_TERM_0";
        } else if (term >= 1 && term <= 6) {
            return "ACCIDENT_TERM_1_OVER";
        }
        throw new BusinessException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "事故有係数期間は0〜6年の間で入力してください。");
    }
}
