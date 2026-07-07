package com.example.quote.controller;

import com.example.quote.dto.request.QuoteCreateRequest;
import com.example.quote.dto.response.QuoteResultResponse;
import com.example.quote.service.QuoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for public auto insurance quotes.
 */
@Tag(name = "見積API", description = "自動車保険の公開見積計算および結果取得API")
@RestController
@RequestMapping("/api/quotes")
public class QuoteController {

    private final QuoteService quoteService;

    @Autowired
    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    /**
     * Creates a new quote based on user input, persists it, and returns the result.
     *
     * @param request the quote creation parameters
     * @return the calculation results
     */
    @Operation(summary = "新規見積作成", description = "入力値を検証し、保険料の計算結果を返却するとともにデータベースに保存します。")
    @PostMapping
    public ResponseEntity<QuoteResultResponse> createQuote(@Valid @RequestBody QuoteCreateRequest request) {
        QuoteResultResponse response = quoteService.createQuote(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves an existing quote by its unique quote number.
     *
     * @param quoteNo the unique quote number (e.g., EST202606230001)
     * @return the quote details and breakdown
     */
    @Operation(summary = "見積結果取得", description = "見積番号をキーとして、保存された見積詳細と内訳明细を取得します。")
    @GetMapping("/{quoteNo}")
    public ResponseEntity<QuoteResultResponse> getQuote(
            @Parameter(description = "見積番号", example = "EST202606230001")
            @PathVariable String quoteNo) {
        QuoteResultResponse response = quoteService.getQuoteByQuoteNo(quoteNo);
        return ResponseEntity.ok(response);
    }
}
