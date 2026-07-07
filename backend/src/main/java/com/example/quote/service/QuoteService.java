package com.example.quote.service;

import com.example.quote.dto.request.QuoteCreateRequest;
import com.example.quote.dto.response.QuoteResultResponse;

import java.util.List;

/**
 * Service interface for quotes.
 */
public interface QuoteService {

    /**
     * Creates a new quote calculation, saves it to database, and returns the response.
     *
     * @param request the conditions to calculate the premium for
     * @return the saved quote result details
     */
    QuoteResultResponse createQuote(QuoteCreateRequest request);

    /**
     * Retrieves an existing quote by its quote number.
     *
     * @param quoteNo the quote number to search for
     * @return the quote result response
     */
    QuoteResultResponse getQuoteByQuoteNo(String quoteNo);

    /**
     * Searches quotes by quote number and creation date range.
     *
     * @param quoteNo the quote number filter (optional, partial match)
     * @param createDateFrom start of creation date range (optional, yyyy-MM-dd)
     * @param createDateTo end of creation date range (optional, yyyy-MM-dd)
     * @return a list of matching quote result responses
     */
    List<QuoteResultResponse> searchQuotes(String quoteNo, String createDateFrom, String createDateTo);

    /**
     * Exports quotes matching filters as a Japanese CSV string.
     *
     * @param quoteNo the quote number filter (optional, partial match)
     * @param createDateFrom start of creation date range (optional, yyyy-MM-dd)
     * @param createDateTo end of creation date range (optional, yyyy-MM-dd)
     * @return CSV string with Japanese headers and value mappings
     */
    String exportQuotesCsv(String quoteNo, String createDateFrom, String createDateTo);

    /**
     * Streams quotes matching filters as a Japanese CSV directly into the given writer.
     * Prevent memory footprint scaling linearly with database records.
     *
     * @param writer the writer to output the CSV characters to
     * @param quoteNo the quote number filter (optional, partial match)
     * @param createDateFrom start of creation date range (optional, yyyy-MM-dd)
     * @param createDateTo end of creation date range (optional, yyyy-MM-dd)
     */
    void exportQuotesCsvStream(java.io.Writer writer, String quoteNo, String createDateFrom, String createDateTo);
}


