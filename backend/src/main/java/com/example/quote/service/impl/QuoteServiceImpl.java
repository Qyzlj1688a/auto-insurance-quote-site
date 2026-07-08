package com.example.quote.service.impl;

import com.example.quote.dto.QuoteCalculationResult;
import com.example.quote.dto.request.QuoteCreateRequest;
import com.example.quote.dto.response.QuoteResultResponse;
import com.example.quote.entity.Quote;
import com.example.quote.entity.QuoteBreakdown;
import com.example.quote.entity.RateMaster;
import com.example.quote.exception.BusinessException;
import com.example.quote.repository.QuoteBreakdownRepository;
import com.example.quote.repository.QuoteRepository;
import com.example.quote.repository.RateMasterRepository;
import com.example.quote.service.PremiumCalculator;
import com.example.quote.service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 見積関連のデータ永続化および計算を処理するサービスクラス。
 */
@Service
@Transactional
public class QuoteServiceImpl implements QuoteService {

    private final QuoteRepository quoteRepository;
    private final QuoteBreakdownRepository quoteBreakdownRepository;
    private final RateMasterRepository rateMasterRepository;
    private final PremiumCalculator premiumCalculator;

    @Autowired
    public QuoteServiceImpl(QuoteRepository quoteRepository,
                            QuoteBreakdownRepository quoteBreakdownRepository,
                            RateMasterRepository rateMasterRepository,
                            PremiumCalculator premiumCalculator) {
        this.quoteRepository = quoteRepository;
        this.quoteBreakdownRepository = quoteBreakdownRepository;
        this.rateMasterRepository = rateMasterRepository;
        this.premiumCalculator = premiumCalculator;
    }

    @Override
    public QuoteResultResponse createQuote(QuoteCreateRequest request) {
        // 1. 相関バリデーションチェックの実行
        validateRequest(request);

        // 2. 有効なすべての料率マスタを取得（単一クエリによるフェッチ戦略）
        List<RateMaster> activeRates = rateMasterRepository.findByActiveTrueOrderByCategoryAscIdAsc();

        // 3. メモリ内の計算エンジンへ保険料計算処理を委譲
        QuoteCalculationResult calcResult = premiumCalculator.calculate(request, activeRates);

        // 4. 一意の見積番号を生成 (EST + yyyyMMdd + 4桁連番)
        String quoteNo = generateQuoteNo();

        // 5. 見積Entityの構築と保存
        Quote quote = new Quote();
        quote.setQuoteNo(quoteNo);
        quote.setDriverAge(request.getDriverAge());
        quote.setLicenseColor(request.getLicenseColor());
        quote.setUsageType(request.getUsageType());
        quote.setAnnualMileage(request.getAnnualMileage());
        quote.setDriverRange(request.getDriverRange());
        quote.setHasCurrentInsurance(request.getHasCurrentInsurance());
        if (Boolean.TRUE.equals(request.getHasCurrentInsurance())) {
            quote.setGrade(request.getGrade());
            quote.setAccidentTerm(request.getAccidentTerm());
        } else {
            quote.setGrade(null);
            quote.setAccidentTerm(null);
        }
        quote.setMaker(request.getMaker());
        quote.setCarName(request.getCarName());
        quote.setFirstRegistrationYm(request.getFirstRegistrationYearMonth());
        quote.setVehicleType(request.getVehicleType());
        quote.setVehicleInsurance(request.getVehicleInsurance());
        quote.setPropertyDamageLimit(request.getPropertyDamageLimit());
        quote.setPersonalInjuryAmount(request.getPersonalInjuryAmount());
        quote.setLawyerOption(request.getLawyerOption());
        quote.setRoadService(request.getRoadService());
        quote.setAnnualPremium(calcResult.getAnnualPremium());
        quote.setMonthlyPremium(calcResult.getMonthlyPremium());

        LocalDateTime now = LocalDateTime.now(java.time.ZoneOffset.UTC);
        quote.setCreatedAt(now);
        quote.setUpdatedAt(now);

        // データベースに保存し、自動採番された見積IDを取得
        Quote savedQuote = quoteRepository.save(quote);
        Long quoteId = savedQuote.getId();

        // 6. 計算内訳に見積IDを設定して一括保存
        List<QuoteBreakdown> breakdowns = calcResult.getBreakdowns();
        for (QuoteBreakdown qb : breakdowns) {
            qb.setQuoteId(quoteId);
        }
        quoteBreakdownRepository.saveAll(breakdowns);

        // 7. レスポンス用DTOへのマッピング
        return mapToResponse(savedQuote, breakdowns);
    }

    @Override
    @Transactional(readOnly = true)
    public QuoteResultResponse getQuoteByQuoteNo(String quoteNo) {
        if (quoteNo == null || quoteNo.trim().isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "見積番号は必須項目です。");
        }
        Quote quote = quoteRepository.findByQuoteNo(quoteNo)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "NOT_FOUND", "指定された見積番号は存在しません。"));

        List<QuoteBreakdown> breakdowns = quoteBreakdownRepository.findByQuoteIdOrderByDisplayOrderAsc(quote.getId());

        return mapToResponse(quote, breakdowns);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuoteResultResponse> searchQuotes(String quoteNo, String createDateFrom, String createDateTo) {
        QuoteSearchCriteria criteria = validateAndParseSearchCriteria(quoteNo, createDateFrom, createDateTo);
        List<Quote> quotes = quoteRepository.searchQuotes(criteria.quoteNo, criteria.fromDate, criteria.toDate);

        if (quotes.isEmpty()) {
            return List.of();
        }

        // 1. 見積IDのリストを収集します (すべての主表ID)
        List<Long> quoteIds = quotes.stream().map(Quote::getId).toList();

        // 2. 関連する明細レコードを一括で取得します (N+1問題の解消)
        List<QuoteBreakdown> allBreakdowns = quoteBreakdownRepository.findByQuoteIdInOrderByDisplayOrderAsc(quoteIds);

        // 3. メモリ上でquoteIdごとにグルーピングします
        Map<Long, List<QuoteBreakdown>> breakdownMap = allBreakdowns.stream()
                .collect(Collectors.groupingBy(QuoteBreakdown::getQuoteId));

        // 4. DTOにマッピングして返却します
        return quotes.stream().map(quote -> {
            List<QuoteBreakdown> breakdowns = breakdownMap.getOrDefault(quote.getId(), List.of());
            return mapToResponse(quote, breakdowns);
        }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public String exportQuotesCsv(String quoteNo, String createDateFrom, String createDateTo) {
        java.io.StringWriter sw = new java.io.StringWriter();
        exportQuotesCsvStream(sw, quoteNo, createDateFrom, createDateTo);
        return sw.toString();
    }

    @Override
    @Transactional(readOnly = true)
    public void exportQuotesCsvStream(java.io.Writer writer, String quoteNo, String createDateFrom, String createDateTo) {
        QuoteSearchCriteria criteria = validateAndParseSearchCriteria(quoteNo, createDateFrom, createDateTo);

        try {
            writer.write("\uFEFF"); // UTF-8 BOM
            writer.write("見積番号,作成日時,年間保険料,月額保険料,運転者年齢,免許証の色,使用目的,年間走行距離,運転者範囲,メーカー,車名,初度登録年月,車両タイプ,車両保険有無,対物賠償制限額,人身傷害補償額,弁護士特約,ロードサービス\r\n");

            int page = 0;
            int pageSize = 500; // メモリ逼迫を防ぐため500件ずつ分割ロード
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            while (true) {
                org.springframework.data.domain.Page<Quote> quotePage = quoteRepository.searchQuotesPaged(
                        criteria.quoteNo,
                        criteria.fromDate,
                        criteria.toDate,
                        org.springframework.data.domain.PageRequest.of(page, pageSize, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"))
                );

                List<Quote> quotes = quotePage.getContent();
                if (quotes.isEmpty()) {
                    break;
                }

                // 一括で関連する明細を取得してN+1問題を回避
                List<Long> quoteIds = quotes.stream().map(Quote::getId).toList();
                List<QuoteBreakdown> breakdowns = quoteBreakdownRepository.findByQuoteIdInOrderByDisplayOrderAsc(quoteIds);
                Map<Long, List<QuoteBreakdown>> breakdownMap = breakdowns.stream()
                        .collect(Collectors.groupingBy(QuoteBreakdown::getQuoteId));

                for (Quote q : quotes) {
                    String createdAtStr = q.getCreatedAt() != null ? q.getCreatedAt().format(outputFormatter) : "";

                    String licenseColorJa = switch (q.getLicenseColor() != null ? q.getLicenseColor() : "") {
                        case "GOLD" -> "ゴールド";
                        case "BLUE" -> "ブルー";
                        case "GREEN" -> "グリーン";
                        default -> q.getLicenseColor() != null ? q.getLicenseColor() : "";
                    };

                    String usageTypeJa = switch (q.getUsageType() != null ? q.getUsageType() : "") {
                        case "PRIVATE" -> "日常・レジャー";
                        case "COMMUTE" -> "通勤・通学";
                        case "BUSINESS" -> "業務使用";
                        default -> q.getUsageType() != null ? q.getUsageType() : "";
                    };

                    String driverRangeJa = switch (q.getDriverRange() != null ? q.getDriverRange() : "") {
                        case "SELF" -> "本人限定";
                        case "COUPLE" -> "夫婦限定";
                        case "FAMILY" -> "家族限定";
                        case "ANYONE" -> "限定なし";
                        default -> q.getDriverRange() != null ? q.getDriverRange() : "";
                    };

                    String vehicleTypeJa = switch (q.getVehicleType() != null ? q.getVehicleType() : "") {
                        case "COMPACT" -> "コンパクトカー";
                        case "SEDAN" -> "セダン";
                        case "MINIVAN" -> "ミニバン";
                        case "SUV" -> "SUV";
                        case "KEI" -> "軽自動車";
                        default -> q.getVehicleType() != null ? q.getVehicleType() : "";
                    };

                    String propertyDamageLimitJa = switch (q.getPropertyDamageLimit() != null ? q.getPropertyDamageLimit() : "") {
                        case "UNLIMITED" -> "無制限";
                        case "THIRTY_MILLION" -> "3,000万円";
                        default -> q.getPropertyDamageLimit() != null ? q.getPropertyDamageLimit() : "";
                    };

                    String personalInjuryAmountJa = switch (q.getPersonalInjuryAmount() != null ? q.getPersonalInjuryAmount() : "") {
                        case "UNLIMITED" -> "無制限";
                        case "THIRTY_MILLION" -> "3,000万円";
                        case "FIFTY_MILLION" -> "5,000万円";
                        default -> q.getPersonalInjuryAmount() != null ? q.getPersonalInjuryAmount() : "";
                    };

                    writer.write(escapeCsvValue(q.getQuoteNo()) + ",");
                    writer.write(escapeCsvValue(createdAtStr) + ",");
                    writer.write((q.getAnnualPremium() != null ? q.getAnnualPremium() : 0) + ",");
                    writer.write((q.getMonthlyPremium() != null ? q.getMonthlyPremium() : 0) + ",");
                    writer.write(q.getDriverAge() + ",");
                    writer.write(escapeCsvValue(licenseColorJa) + ",");
                    writer.write(escapeCsvValue(usageTypeJa) + ",");
                    writer.write(q.getAnnualMileage() + ",");
                    writer.write(escapeCsvValue(driverRangeJa) + ",");
                    writer.write(escapeCsvValue(q.getMaker()) + ",");
                    writer.write(escapeCsvValue(q.getCarName()) + ",");
                    writer.write(escapeCsvValue(q.getFirstRegistrationYm()) + ",");
                    writer.write(escapeCsvValue(vehicleTypeJa) + ",");
                    writer.write((Boolean.TRUE.equals(q.getVehicleInsurance()) ? "あり" : "なし") + ",");
                    writer.write(escapeCsvValue(propertyDamageLimitJa) + ",");
                    writer.write(escapeCsvValue(personalInjuryAmountJa) + ",");
                    writer.write((Boolean.TRUE.equals(q.getLawyerOption()) ? "あり" : "なし") + ",");
                    writer.write((Boolean.TRUE.equals(q.getRoadService()) ? "あり" : "なし") + "\r\n");
                }

                writer.flush(); // 大容量データの書き込み中にメモリが溢れるのを防ぐため、バッチごとにクライアントへ送信
                page++;
            }
        } catch (java.io.IOException e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "SYSTEM_ERROR", "CSV出力の書き出し中にエラーが発生しました。");
        }
    }

    /**
     * CSVの値を安全にエスケープおよびエンコードします。
     * RFC 4180に準拠し、カンマ、ダブルクォーテーション、改行が含まれる場合はダブルクォーテーションで囲み、
     * 内部のダブルクォーテーションを二重化します。
     * また、式注入（Formula Injection）を防ぐため、特定の文字で始まる場合はシングルクォーテーションを付加します。
     *
     * @param value エスケープ対象の値
     * @return エスケープ済みの文字列
     */
    private String escapeCsvValue(String value) {
        if (value == null) {
            return "";
        }
        
        String escaped = value;
        // 数式注入脆弱性の対策：特定の文字で始まる場合はシングルクォーテーションを先頭に付加します
        if (escaped.startsWith("=") || escaped.startsWith("+") || escaped.startsWith("-") || escaped.startsWith("@")) {
            escaped = "'" + escaped;
        }

        // カンマ、ダブルクォーテーション、改行が含まれる場合はダブルクォーテーションで囲みます
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n") || escaped.contains("\r")) {
            escaped = escaped.replace("\"", "\"\"");
            return "\"" + escaped + "\"";
        }
        
        return escaped;
    }

    private QuoteSearchCriteria validateAndParseSearchCriteria(String quoteNo, String startDateStr, String endDateStr) {
        LocalDateTime fromDate = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
        LocalDateTime toDate = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        ZoneId jstZone = ZoneId.of("Asia/Tokyo");

        if (startDateStr != null && !startDateStr.trim().isEmpty()) {
            try {
                // 日本時間の 00:00:00 を取得し、それをUTCのLocalDateTimeに変換します
                LocalDate localDate = LocalDate.parse(startDateStr.trim(), formatter);
                ZonedDateTime jstStart = localDate.atStartOfDay(jstZone);
                fromDate = jstStart.withZoneSameInstant(java.time.ZoneOffset.UTC).toLocalDateTime();
            } catch (DateTimeParseException e) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "検索開始日の形式が正しくありません。YYYY-MM-DD形式で入力してください。");
            }
        }
        if (endDateStr != null && !endDateStr.trim().isEmpty()) {
            try {
                // 日本時間の 23:59:59.999999999 を取得し、それをUTCのLocalDateTimeに変換します
                LocalDate localDate = LocalDate.parse(endDateStr.trim(), formatter);
                ZonedDateTime jstEnd = localDate.atTime(java.time.LocalTime.MAX).atZone(jstZone);
                toDate = jstEnd.withZoneSameInstant(java.time.ZoneOffset.UTC).toLocalDateTime();
            } catch (DateTimeParseException e) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "検索終了日の形式が正しくありません。YYYY-MM-DD形式で入力してください。");
            }
        }

        String filterQuoteNo = (quoteNo != null && !quoteNo.trim().isEmpty()) ? quoteNo.trim() : "";
        return new QuoteSearchCriteria(filterQuoteNo, fromDate, toDate);
    }

    private static class QuoteSearchCriteria {
        final String quoteNo;
        final LocalDateTime fromDate;
        final LocalDateTime toDate;

        QuoteSearchCriteria(String quoteNo, LocalDateTime fromDate, LocalDateTime toDate) {
            this.quoteNo = quoteNo;
            this.fromDate = fromDate;
            this.toDate = toDate;
        }
    }

    private void validateRequest(QuoteCreateRequest request) {
        // 複数項目にまたがる相関バリデーションチェック
        if (Boolean.TRUE.equals(request.getHasCurrentInsurance())) {
            if (request.getGrade() == null) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "現在保険に加入している場合、等級は必須項目です。");
            }
            if (request.getAccidentTerm() == null) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "現在保険に加入している場合、事故有係数期間は必須項目です。");
            }
        }

        // 初度登録年月の未来日付チェック
        try {
            YearMonth regYm = YearMonth.parse(request.getFirstRegistrationYearMonth(), DateTimeFormatter.ofPattern("yyyy-MM"));
            // 日本時間（東京タイムゾーン）を基準に現在の年月を取得して未来チェックを実施
            YearMonth currentYm = YearMonth.now(ZoneId.of("Asia/Tokyo"));
            if (regYm.isAfter(currentYm)) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "初度登録年月は未来の年月を入力できません。");
            }
        } catch (DateTimeParseException e) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "初度登録年月はYYYY-MM形式で入力してください。");
        }
    }

    /**
     * 見積番号を動的に採番します (フォーマット: EST + yyyyMMdd + 4桁連番)
     * 補助テーブルを使用せず、PostgreSQLのトランザクションレベルの勧告ロック（Advisory Lock）を用いて
     * 並行トランザクション間の重複を防止します。
     */
    private String generateQuoteNo() {
        // 日本の東京時間を基準に本日日付を取得します
        java.time.LocalDate todayJst = java.time.LocalDate.now(java.time.ZoneId.of("Asia/Tokyo"));
        String dateStr = todayJst.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long lockKey = Long.parseLong(dateStr);

        // トランザクションロックを確保（当日日付をキーにして競合をシリアライズ）
        quoteRepository.acquireAdvisoryXactLock(lockKey);

        // 本日の既存見積の最大連番を取得します
        int maxSerial = quoteRepository.getMaxSerialForDate(dateStr);
        int nextSerial = maxSerial + 1;

        return String.format("EST%s%04d", dateStr, nextSerial);
    }

    private QuoteResultResponse mapToResponse(Quote savedQuote, List<QuoteBreakdown> breakdowns) {
        QuoteResultResponse response = new QuoteResultResponse();
        response.setQuoteNo(savedQuote.getQuoteNo());
        response.setAnnualPremium(savedQuote.getAnnualPremium());
        response.setMonthlyPremium(savedQuote.getMonthlyPremium());

        response.setDriverAge(savedQuote.getDriverAge());
        response.setLicenseColor(savedQuote.getLicenseColor());
        response.setUsageType(savedQuote.getUsageType());
        response.setAnnualMileage(savedQuote.getAnnualMileage());
        response.setDriverRange(savedQuote.getDriverRange());
        response.setHasCurrentInsurance(savedQuote.getHasCurrentInsurance());
        response.setGrade(savedQuote.getGrade());
        response.setAccidentTerm(savedQuote.getAccidentTerm());
        response.setMaker(savedQuote.getMaker());
        response.setCarName(savedQuote.getCarName());
        response.setFirstRegistrationYearMonth(savedQuote.getFirstRegistrationYm());
        response.setVehicleType(savedQuote.getVehicleType());
        response.setVehicleInsurance(savedQuote.getVehicleInsurance());
        response.setPropertyDamageLimit(savedQuote.getPropertyDamageLimit());
        response.setPersonalInjuryAmount(savedQuote.getPersonalInjuryAmount());
        response.setLawyerOption(savedQuote.getLawyerOption());
        response.setRoadService(savedQuote.getRoadService());

        List<QuoteResultResponse.BreakdownResponse> brList = breakdowns.stream().map(qb -> {
            QuoteResultResponse.BreakdownResponse br = new QuoteResultResponse.BreakdownResponse();
            br.setItemCode(qb.getItemCode());
            br.setItemName(qb.getItemName());
            br.setRate(qb.getRate());
            br.setAmount(qb.getAmount());
            br.setDisplayOrder(qb.getDisplayOrder());
            return br;
        }).toList();

        response.setBreakdowns(brList);

        // ISO-8601形式のオフセット日時に変換 (例: 2026-06-23T10:15:30Z)
        ZonedDateTime zdt = savedQuote.getCreatedAt().atZone(java.time.ZoneOffset.UTC);
        response.setCreatedAt(zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        return response;
    }
}
