package com.xmcy.cryptorecommendation.controller;

import com.xmcy.cryptorecommendation.dto.CryptoNormalizedDTO;
import com.xmcy.cryptorecommendation.dto.CryptoStatsDTO;
import com.xmcy.cryptorecommendation.service.CryptoRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller - Crypto Recommendation
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/cryptos/recommendations")
public class CryptoRecommendationController {

    private final CryptoRecommendationService cryptoRecommendationService;

    @Operation(summary = "Get all sorted Cryptos by normalized range(price) descending(i.e. (max-min)/min).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All sorted Cryptos by normalized range descending have been provided.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "404", description = "Crypto not found.")})
    @GetMapping("normalized-price")
    public ResponseEntity<List<CryptoNormalizedDTO>> sortedNormalizedCryptosDescending(
            @Parameter(description = "Date format: yyyy-MM-dd")
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @Parameter(description = "Date format: yyyy-MM-dd")
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate) {
        return ResponseEntity.ok(cryptoRecommendationService.getCryptoListSortedDescendingNormalizedPrice(fromDate, toDate));
    }

    @Operation(summary = "Get Crypto stats (oldest/newest/min/max) for specific Crypto Symbol.",
            description = """
                    By default, Crypto stats are provided for the entire time period for the specified Crypto Symbol,
                    if it exists.
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Crypto stats has been provided.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CryptoStatsDTO.class))),
            @ApiResponse(responseCode = "404", description = "Crypto Stats not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @GetMapping("stats/{symbol}")
    public ResponseEntity<CryptoStatsDTO> getCryptoStatsByCryptoSymbol(
            @PathVariable("symbol") String symbol,
            @Parameter(description = "Date format: yyyy-MM-dd")
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @Parameter(description = "Date format: yyyy-MM-dd")
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate) {
        return ResponseEntity.ok(cryptoRecommendationService.getCryptoStats(symbol, fromDate, toDate));
    }

    @Operation(summary = "Get Crypto Price with highest normalized range for specific day.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Crypto Price with highest normalized range for specific day has been provided.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CryptoNormalizedDTO.class))),
            @ApiResponse(responseCode = "404", description = "Crypto Price not found for this day.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @GetMapping("highest-normalized-price/{date}")
    public ResponseEntity<CryptoNormalizedDTO> highestCryptoNormalizedPriceRangeBySpecificDay(
            @Parameter(description = "Date format: yyyy-MM-dd")
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return ResponseEntity.ok(cryptoRecommendationService.getCryptoHighestNormalizedPrice(date));
    }
}
