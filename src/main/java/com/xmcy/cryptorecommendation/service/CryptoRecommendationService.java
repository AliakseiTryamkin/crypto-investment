package com.xmcy.cryptorecommendation.service;

import com.xmcy.cryptorecommendation.dto.CryptoNormalizedDTO;
import com.xmcy.cryptorecommendation.dto.CryptoStatsDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for processing crypto statistics.
 */
public interface CryptoRecommendationService {

    /**
     * The method calculates normalized price for each Crypto with specified date range(if provided)
     * and return data as descending sorted list.
     */
    List<CryptoNormalizedDTO> getCryptoListSortedDescendingNormalizedPrice(LocalDate fromDate, LocalDate toDate);

    /**
     * The method save stats for specific Crypto type by specified range date(range is optional,
     * by default range is month before today).
     */
    void saveCryptoStats(String symbol, LocalDateTime fromDate, LocalDateTime toDate);

    /**
     * The method returns stats for specific Crypto type by specified range date(range is optional,
     * by default range is the entire time period, if not current dates will return stats with 'zero' points).
     * If some Crypto stats not exist, will return zero for those stats points
     */
    CryptoStatsDTO getCryptoStats(String symbol, LocalDate fromDate, LocalDate toDate);

    /**
     * The method returns Crypto type with highest normalized range for the specified day.
     */
    CryptoNormalizedDTO getCryptoHighestNormalizedPrice(LocalDate date);

}
