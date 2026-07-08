package com.example.quote.service;

import com.example.quote.dto.request.QuoteCreateRequest;
import com.example.quote.dto.response.QuoteResultResponse;

import java.util.List;

/**
 * 見積処理用サービスインターフェース。
 */
public interface QuoteService {

    /**
     * 新規の見積計算を実行し、データベースに保存した上で結果レスポンスを返却します。
     *
     * @param request 保険料計算用の見積依頼パラメータ
     * @return 保存された見積結果の詳細情報
     */
    QuoteResultResponse createQuote(QuoteCreateRequest request);

    /**
     * 見積番号をキーとして、既存の見積詳細情報を取得します。
     *
     * @param quoteNo 検索対象の見積番号
     * @return 見積結果レスポンス
     */
    QuoteResultResponse getQuoteByQuoteNo(String quoteNo);

    /**
     * 見積番号および作成日の期間を指定して見積一覧を検索します。
     *
     * @param quoteNo 見積番号フィルタ（任意、部分一致）
     * @param createDateFrom 作成日の検索開始期間（任意、yyyy-MM-dd）
     * @param createDateTo 作成日の検索終了期間（任意、yyyy-MM-dd）
     * @return 該当する見積結果レスポンスのリスト
     */
    List<QuoteResultResponse> searchQuotes(String quoteNo, String createDateFrom, String createDateTo);

    /**
     * フィルタ条件に合致する見積一覧情報を日本語ヘッダーのCSV文字列としてエクスポートします。
     *
     * @param quoteNo 見積番号フィルタ（任意、部分一致）
     * @param createDateFrom 作成日の検索開始期間（任意、yyyy-MM-dd）
     * @param createDateTo 作成日の検索終了期間（任意、yyyy-MM-dd）
     * @return 日本語ヘッダーと値のマッピングを含むCSV文字列
     */
    String exportQuotesCsv(String quoteNo, String createDateFrom, String createDateTo);

    /**
     * 検索条件に合致する見積一覧情報を日本語ヘッダーのCSV形式でWriterへ直接書き出します（ストリーミング出力）。
     * 大容量データのエクスポート時にメモリ使用量が肥大化するのを防ぎます。
     *
     * @param writer CSV文字を出力するWriterオブジェクト
     * @param quoteNo 見積番号フィルタ（任意、部分一致）
     * @param createDateFrom 作成日の検索開始期間（任意、yyyy-MM-dd）
     * @param createDateTo 作成日の検索終了期間（任意、yyyy-MM-dd）
     */
    void exportQuotesCsvStream(java.io.Writer writer, String quoteNo, String createDateFrom, String createDateTo);
}


