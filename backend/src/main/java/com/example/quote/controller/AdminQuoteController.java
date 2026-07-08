package com.example.quote.controller;

import com.example.quote.dto.response.QuoteResultResponse;
import com.example.quote.service.QuoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 管理者向けの見積検索・詳細取得・CSV出力APIコントローラー。
 */
@Tag(name = "管理用見積API", description = "管理者用の見積情報検索・詳細取得・CSV出力API")
@RestController
@RequestMapping("/api/admin")
public class AdminQuoteController {

    private final QuoteService quoteService;

    public AdminQuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    /**
     * 指定された検索条件（見積番号、作成期間）に基づいて見積一覧を検索します。
     */
    @Operation(summary = "見積一覧検索", description = "見積番号や作成日時（期間）を指定して見積情報を検索します。")
    @GetMapping("/quotes")
    public ResponseEntity<List<QuoteResultResponse>> searchQuotes(
            @Parameter(description = "見積番号", example = "EST202606230001")
            @RequestParam(required = false) String quoteNo,
            @Parameter(description = "作成日（開始） (YYYY-MM-DD)", example = "2026-06-01")
            @RequestParam(required = false) String createDateFrom,
            @Parameter(description = "作成日（終了） (YYYY-MM-DD)", example = "2026-06-30")
            @RequestParam(required = false) String createDateTo) {
        List<QuoteResultResponse> response = quoteService.searchQuotes(quoteNo, createDateFrom, createDateTo);
        return ResponseEntity.ok(response);
    }

    /**
     * 見積番号をキーとして、特定の見積詳細情報および計算内訳を取得します。
     */
    @Operation(summary = "見積詳細取得", description = "見積番号をキーとして見積詳細および計算内訳を取得します。")
    @GetMapping("/quotes/{quoteNo}")
    public ResponseEntity<QuoteResultResponse> getQuoteDetail(
            @Parameter(description = "見積番号", example = "EST202606230001")
            @PathVariable String quoteNo) {
        QuoteResultResponse response = quoteService.getQuoteByQuoteNo(quoteNo);
        return ResponseEntity.ok(response);
    }

    /**
     * 検索条件に合致する見積一覧情報をCSVファイルストリームとしてエクスポートします。
     */
    @Operation(summary = "見積一覧CSV出力", description = "条件に合致する見積一覧情報を日文CSV形式で出力します。")
    @GetMapping(value = "/quotes.csv", produces = "text/csv")
    public ResponseEntity<org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody> exportQuotesCsv(
            @Parameter(description = "見積番号", example = "EST202606230001")
            @RequestParam(required = false) String quoteNo,
            @Parameter(description = "作成日（開始） (YYYY-MM-DD)", example = "2026-06-01")
            @RequestParam(required = false) String createDateFrom,
            @Parameter(description = "作成日（終了） (YYYY-MM-DD)", example = "2026-06-30")
            @RequestParam(required = false) String createDateTo) {
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        
        String filename = "quotes.csv";
        String encodedFilename = org.springframework.web.util.UriUtils.encode(filename, StandardCharsets.UTF_8.name());
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"; filename*=UTF-8''" + encodedFilename);

        org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody responseBody = outputStream -> {
            try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
                quoteService.exportQuotesCsvStream(writer, quoteNo, createDateFrom, createDateTo);
            }
        };

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseBody);
    }
}
