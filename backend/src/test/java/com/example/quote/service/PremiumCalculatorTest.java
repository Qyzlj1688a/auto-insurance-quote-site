package com.example.quote.service;

import com.example.quote.dto.QuoteCalculationResult;
import com.example.quote.dto.request.QuoteCreateRequest;
import com.example.quote.entity.RateMaster;
import com.example.quote.service.impl.PremiumCalculatorImpl;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PremiumCalculatorTest {

    private final PremiumCalculator premiumCalculator = new PremiumCalculatorImpl();
    private final List<RateMaster> mockRates = setupMockRates();

    @Test
    void testUT001_LowRiskCase() {
        QuoteCreateRequest request = new QuoteCreateRequest();
        request.setDriverAge(35);
        request.setLicenseColor("GOLD");
        request.setUsageType("PRIVATE");
        request.setAnnualMileage(8000);
        request.setDriverRange("SELF");
        request.setHasCurrentInsurance(true);
        request.setGrade(20);
        request.setAccidentTerm(0);
        request.setMaker("トヨタ");
        request.setCarName("プリウス");
        request.setFirstRegistrationYearMonth("2020-05");
        request.setVehicleType("SEDAN");
        request.setVehicleInsurance(false);
        request.setPropertyDamageLimit("UNLIMITED");
        request.setPersonalInjuryAmount("THIRTY_MILLION");
        request.setLawyerOption(false);
        request.setRoadService(false);

        QuoteCalculationResult result = premiumCalculator.calculate(request, mockRates);

        assertNotNull(result);
        assertEquals(37400, result.getAnnualPremium()); // 32400 + 5000 = 37400
        assertEquals(3120, result.getMonthlyPremium()); // 37400 / 12 = 3116.67 -> 3120
    }

    @Test
    void testUT002_HighRiskCase() {
        QuoteCreateRequest request = new QuoteCreateRequest();
        request.setDriverAge(18);
        request.setLicenseColor("GREEN");
        request.setUsageType("BUSINESS");
        request.setAnnualMileage(15000);
        request.setDriverRange("ANYONE");
        request.setHasCurrentInsurance(true);
        request.setGrade(3);
        request.setAccidentTerm(3);
        request.setMaker("ホンダ");
        request.setCarName("ヴェゼル");
        request.setFirstRegistrationYearMonth("2022-10");
        request.setVehicleType("SUV");
        request.setVehicleInsurance(true);
        request.setPropertyDamageLimit("UNLIMITED");
        request.setPersonalInjuryAmount("UNLIMITED");
        request.setLawyerOption(true);
        request.setRoadService(true);

        QuoteCalculationResult result = premiumCalculator.calculate(request, mockRates);

        assertNotNull(result);
        assertEquals(317830, result.getAnnualPremium()); // 272329.2 + 45500 = 317829.2 -> 317830
        assertEquals(26490, result.getMonthlyPremium()); // 317830 / 12 = 26485.83 -> 26490
    }

    @Test
    void testUT003_AgeBoundaries() {
        // 年齢 25歳（係数1.60）
        QuoteCreateRequest r1 = createBaseRequest();
        r1.setDriverAge(25);
        QuoteCalculationResult res1 = premiumCalculator.calculate(r1, mockRates);
        
        // 年齢 26歳（係数1.25）
        QuoteCreateRequest r2 = createBaseRequest();
        r2.setDriverAge(26);
        QuoteCalculationResult res2 = premiumCalculator.calculate(r2, mockRates);

        // 年齢 34歳（係数1.25）
        QuoteCreateRequest r3 = createBaseRequest();
        r3.setDriverAge(34);
        QuoteCalculationResult res3 = premiumCalculator.calculate(r3, mockRates);

        // 年齢 35歳（係数1.00）
        QuoteCreateRequest r4 = createBaseRequest();
        r4.setDriverAge(35);
        QuoteCalculationResult res4 = premiumCalculator.calculate(r4, mockRates);

        // 年齢 59歳（係数1.00）
        QuoteCreateRequest r5 = createBaseRequest();
        r5.setDriverAge(59);
        QuoteCalculationResult res5 = premiumCalculator.calculate(r5, mockRates);

        // 年齢 60歳（係数1.20）
        QuoteCreateRequest r6 = createBaseRequest();
        r6.setDriverAge(60);
        QuoteCalculationResult res6 = premiumCalculator.calculate(r6, mockRates);

        // 25歳（係数1.60）vs 26歳（係数1.25）の境界値確認
        // UT-001基準：35歳のベース保険料は32400円。25歳係数1.6 → 32400 * 1.6 = 51840。51840 + 5000 = 56840
        assertEquals(56840, res1.getAnnualPremium());
        
        // 26歳（係数1.25）：32400 * 1.25 = 40500。40500 + 5000 = 45500
        assertEquals(45500, res2.getAnnualPremium());

        // 34歳（係数1.25）：26歳と同一係数
        assertEquals(45500, res3.getAnnualPremium());

        // 35歳（係数1.00）：基準値
        assertEquals(37400, res4.getAnnualPremium());

        // 59歳（係数1.00）：35歳と同一係数
        assertEquals(37400, res5.getAnnualPremium());

        // 60歳（係数1.20）：32400 * 1.20 = 38880。38880 + 5000 = 43880
        assertEquals(43880, res6.getAnnualPremium());
    }

    @Test
    void testUT004_MileageBoundaries() {
        // 走行距離 5000km（係数0.95）
        QuoteCreateRequest r1 = createBaseRequest();
        r1.setAnnualMileage(5000);
        QuoteCalculationResult res1 = premiumCalculator.calculate(r1, mockRates);

        // 走行距離 5001km（係数1.00）
        QuoteCreateRequest r2 = createBaseRequest();
        r2.setAnnualMileage(5001);
        QuoteCalculationResult res2 = premiumCalculator.calculate(r2, mockRates);

        // 走行距離 10000km（係数1.00）
        QuoteCreateRequest r3 = createBaseRequest();
        r3.setAnnualMileage(10000);
        QuoteCalculationResult res3 = premiumCalculator.calculate(r3, mockRates);

        // 走行距離 10001km（係数1.15）
        QuoteCreateRequest r4 = createBaseRequest();
        r4.setAnnualMileage(10001);
        QuoteCalculationResult res4 = premiumCalculator.calculate(r4, mockRates);

        // 5000km（係数0.95）：走行距離係数適用前ベース32400。32400 * 0.95 = 30780。30780 + 5000 = 35780
        assertEquals(35780, res1.getAnnualPremium());

        // 5001km（係数1.00）：ベース32400 + 5000 = 37400
        assertEquals(37400, res2.getAnnualPremium());

        // 10000km（係数1.00）：ベース32400 + 5000 = 37400
        assertEquals(37400, res3.getAnnualPremium());

        // 10001km（係数1.15）：32400 * 1.15 = 37260。37260 + 5000 = 42260
        assertEquals(42260, res4.getAnnualPremium());
    }

    @Test
    void testUT005_GradeBoundaries() {
        // 等級 5等級（係数1.30）
        QuoteCreateRequest r1 = createBaseRequest();
        r1.setGrade(5);
        QuoteCalculationResult res1 = premiumCalculator.calculate(r1, mockRates);

        // 等級 6等級（係数1.10）
        QuoteCreateRequest r2 = createBaseRequest();
        r2.setGrade(6);
        QuoteCalculationResult res2 = premiumCalculator.calculate(r2, mockRates);

        // 等級 10等級（係数1.10）
        QuoteCreateRequest r3 = createBaseRequest();
        r3.setGrade(10);
        QuoteCalculationResult res3 = premiumCalculator.calculate(r3, mockRates);

        // 等級 11等級（係数0.95）
        QuoteCreateRequest r4 = createBaseRequest();
        r4.setGrade(11);
        QuoteCalculationResult res4 = premiumCalculator.calculate(r4, mockRates);

        // 等級 15等級（係数0.95）
        QuoteCreateRequest r5 = createBaseRequest();
        r5.setGrade(15);
        QuoteCalculationResult res5 = premiumCalculator.calculate(r5, mockRates);

        // 等級 16等級（係数0.80）
        QuoteCreateRequest r6 = createBaseRequest();
        r6.setGrade(16);
        QuoteCalculationResult res6 = premiumCalculator.calculate(r6, mockRates);

        // 等級係数除外ベース（20等級は係数0.8）：32400 ÷ 0.8 = 40500
        // 5等級（係数1.30）：40500 * 1.3 = 52650。52650 + 5000 = 57650
        assertEquals(57650, res1.getAnnualPremium());

        // 6等級（係数1.10）：40500 * 1.1 = 44550。44550 + 5000 = 49550
        assertEquals(49550, res2.getAnnualPremium());

        // 10等級（係数1.10）：6等級と同一係数。44550 + 5000 = 49550
        assertEquals(49550, res3.getAnnualPremium());

        // 11等級（係数0.95）：40500 * 0.95 = 38475。38475 + 5000 = 43475。10円未満四捨五入 → 43480
        assertEquals(43480, res4.getAnnualPremium());

        // 15等級（係数0.95）：11等級と同一係数 → 43480
        assertEquals(43480, res5.getAnnualPremium());

        // 16等級（係数0.80）：40500 * 0.8 = 32400。32400 + 5000 = 37400
        assertEquals(37400, res6.getAnnualPremium());
    }

    @Test
    void testUT006_RoundingHandling() {
        QuoteCreateRequest request = createBaseRequest();
        // 端数が発生する入力値を設定する：
        // 18歳（1.6）、グリーン（1.1）、業務（1.25）、15000km（1.15）、限定なし（1.2）、3等級（1.3）、事故3年（1.2）、SUV（1.15）
        // 係数積算：50000 * 1.6 * 1.1 * 1.25 * 1.15 * 1.2 * 1.3 * 1.2 * 1.15 = 272329.2
        request.setDriverAge(18);
        request.setLicenseColor("GREEN");
        request.setUsageType("BUSINESS");
        request.setAnnualMileage(15000);
        request.setDriverRange("ANYONE");
        request.setGrade(3);
        request.setAccidentTerm(3);
        request.setVehicleType("SUV");
        request.setVehicleInsurance(true);
        request.setPropertyDamageLimit("UNLIMITED");
        request.setPersonalInjuryAmount("UNLIMITED");
        request.setLawyerOption(true);
        request.setRoadService(true);

        QuoteCalculationResult result = premiumCalculator.calculate(request, mockRates);

        // 272329.2 + 45500 = 317829.2 -> 317830.
        assertEquals(317830, result.getAnnualPremium());

        // 317830 / 12 = 26485.833... -> 26490.
        assertEquals(26490, result.getMonthlyPremium());
    }

    private QuoteCreateRequest createBaseRequest() {
        QuoteCreateRequest request = new QuoteCreateRequest();
        request.setDriverAge(35);
        request.setLicenseColor("GOLD");
        request.setUsageType("PRIVATE");
        request.setAnnualMileage(8000);
        request.setDriverRange("SELF");
        request.setHasCurrentInsurance(true);
        request.setGrade(20);
        request.setAccidentTerm(0);
        request.setMaker("トヨタ");
        request.setCarName("プリウス");
        request.setFirstRegistrationYearMonth("2020-05");
        request.setVehicleType("SEDAN");
        request.setVehicleInsurance(false);
        request.setPropertyDamageLimit("UNLIMITED");
        request.setPersonalInjuryAmount("THIRTY_MILLION");
        request.setLawyerOption(false);
        request.setRoadService(false);
        return request;
    }

    private RateMaster createRateMaster(String category, String itemCode, String itemName, String rate, Integer amount) {
        RateMaster rm = new RateMaster();
        rm.setCategory(category);
        rm.setItemCode(itemCode);
        rm.setItemName(itemName);
        rm.setRate(rate != null ? new BigDecimal(rate) : null);
        rm.setAmount(amount);
        rm.setActive(true);
        return rm;
    }

    private List<RateMaster> setupMockRates() {
        List<RateMaster> list = new ArrayList<>();
        list.add(createRateMaster("BASE_PREMIUM", "BASE", "基本保険料", null, 50000));
        
        list.add(createRateMaster("AGE", "AGE_18_25", "18歳〜25歳", "1.600", null));
        list.add(createRateMaster("AGE", "AGE_26_34", "26歳〜34歳", "1.250", null));
        list.add(createRateMaster("AGE", "AGE_35_59", "35歳〜59歳", "1.000", null));
        list.add(createRateMaster("AGE", "AGE_60_OVER", "60歳以上", "1.200", null));

        list.add(createRateMaster("LICENSE", "GOLD", "ゴールド", "0.900", null));
        list.add(createRateMaster("LICENSE", "BLUE", "ブルー", "1.000", null));
        list.add(createRateMaster("LICENSE", "GREEN", "グリーン", "1.100", null));

        list.add(createRateMaster("USAGE", "PRIVATE", "日常・レジャー", "1.000", null));
        list.add(createRateMaster("USAGE", "COMMUTE", "通勤・通学", "1.100", null));
        list.add(createRateMaster("USAGE", "BUSINESS", "業務使用", "1.250", null));

        list.add(createRateMaster("MILEAGE", "MILEAGE_0_5000", "0km〜5,000km", "0.950", null));
        list.add(createRateMaster("MILEAGE", "MILEAGE_5001_10000", "5,001km〜10,000km", "1.000", null));
        list.add(createRateMaster("MILEAGE", "MILEAGE_10001_OVER", "10,001km以上", "1.150", null));

        list.add(createRateMaster("DRIVER_RANGE", "SELF", "本人限定", "0.900", null));
        list.add(createRateMaster("DRIVER_RANGE", "COUPLE", "夫婦限定", "0.950", null));
        list.add(createRateMaster("DRIVER_RANGE", "FAMILY", "家族限定", "1.050", null));
        list.add(createRateMaster("DRIVER_RANGE", "ANYONE", "限定なし", "1.200", null));

        list.add(createRateMaster("GRADE", "GRADE_1_5", "1等級〜5等級", "1.300", null));
        list.add(createRateMaster("GRADE", "GRADE_6_10", "6等級〜10等級", "1.100", null));
        list.add(createRateMaster("GRADE", "GRADE_11_15", "11等級〜15等級", "0.950", null));
        list.add(createRateMaster("GRADE", "GRADE_16_20", "16等級〜20等級", "0.800", null));

        list.add(createRateMaster("ACCIDENT_TERM", "ACCIDENT_TERM_0", "事故有係数適用期間 0年", "1.000", null));
        list.add(createRateMaster("ACCIDENT_TERM", "ACCIDENT_TERM_1_OVER", "事故有係数適用期間 1年以上", "1.200", null));

        list.add(createRateMaster("VEHICLE_TYPE", "KEI", "軽自動車", "0.900", null));
        list.add(createRateMaster("VEHICLE_TYPE", "COMPACT", "コンパクト", "0.950", null));
        list.add(createRateMaster("VEHICLE_TYPE", "SEDAN", "セダン", "1.000", null));
        list.add(createRateMaster("VEHICLE_TYPE", "MINIVAN", "ミニバン", "1.100", null));
        list.add(createRateMaster("VEHICLE_TYPE", "SUV", "SUV", "1.150", null));

        list.add(createRateMaster("VEHICLE_INSURANCE", "FALSE", "車両保険なし", null, 0));
        list.add(createRateMaster("VEHICLE_INSURANCE", "TRUE", "車両保険あり", null, 30000));

        list.add(createRateMaster("PROPERTY_DAMAGE_LIMIT", "THIRTY_MILLION", "対物補償 3,000万円", null, 0));
        list.add(createRateMaster("PROPERTY_DAMAGE_LIMIT", "UNLIMITED", "対物補償 無制限", null, 5000));

        list.add(createRateMaster("PERSONAL_INJURY_AMOUNT", "THIRTY_MILLION", "人身傷害 3,000万円", null, 0));
        list.add(createRateMaster("PERSONAL_INJURY_AMOUNT", "FIFTY_MILLION", "人身傷害 5,000万円", null, 3000));
        list.add(createRateMaster("PERSONAL_INJURY_AMOUNT", "UNLIMITED", "人身傷害 無制限", null, 7000));

        list.add(createRateMaster("LAWYER_OPTION", "FALSE", "弁護士特約なし", null, 0));
        list.add(createRateMaster("LAWYER_OPTION", "TRUE", "弁護士特約あり", null, 2000));

        list.add(createRateMaster("ROAD_SERVICE", "FALSE", "ロードサービスなし", null, 0));
        list.add(createRateMaster("ROAD_SERVICE", "TRUE", "ロードサービスあり", null, 1500));
        return list;
    }
}
