package com.xmcy.cryptorecommendation.service.impl;

import com.xmcy.cryptorecommendation.dto.CryptoNormalizedDTO;
import com.xmcy.cryptorecommendation.dto.CryptoPriceDTO;
import com.xmcy.cryptorecommendation.dto.CryptoStatsDTO;
import com.xmcy.cryptorecommendation.mapper.CryptoMapper;
import com.xmcy.cryptorecommendation.mapper.CryptoStatsMapper;
import com.xmcy.cryptorecommendation.repository.CryptoRepository;
import com.xmcy.cryptorecommendation.repository.CryptoStatsRepository;
import com.xmcy.cryptorecommendation.service.CryptoRecommendationService;
import com.xmcy.cryptorecommendation.service.CryptoValidationService;
import com.xmcy.cryptorecommendation.util.LocalDateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CryptoRecommendationServiceImpl implements CryptoRecommendationService {

    private final CryptoValidationService cryptoValidationService;
    private final CryptoRepository cryptoRepository;
    private final CryptoStatsRepository cryptoStatsRepository;
    private final CryptoMapper cryptoMapper;
    private final CryptoStatsMapper cryptoStatsMapper;

    @Override
    public List<CryptoNormalizedDTO> getCryptoListSortedDescendingNormalizedPrice(LocalDate fromDate, LocalDate toDate) {
        var symbols = cryptoRepository.getAllSymbols();

        if (CollectionUtils.isEmpty(symbols)) {
            log.info("Crypto symbols do not exist, please check that cryptos were loaded");
            return Collections.emptyList();
        }

        var normalizedCryptoPriceList = new ArrayList<CryptoNormalizedDTO>();
        for (String symbol : symbols) {
            var cryptoNormalizedRange = normalizeCryptoPrice(symbol, fromDate, toDate);
            normalizedCryptoPriceList.add(cryptoNormalizedRange);
        }
        descendingSortOrderByPrice(normalizedCryptoPriceList);
        return normalizedCryptoPriceList;
    }

    @CacheEvict(value = "cryptoStats", allEntries = true)
    @Override
    public void saveCryptoStats(String symbol, LocalDateTime fromDate, LocalDateTime toDate) {
        cryptoValidationService.checkCryptoSupported(symbol);
        var cryptoStatsDTO = calculateCryptoStats(symbol, fromDate, toDate);
        cryptoStatsRepository.save(cryptoStatsMapper.mapDtoToEntity(cryptoStatsDTO));
    }

    @Cacheable("cryptoStats")
    @Override
    public CryptoStatsDTO getCryptoStats(String symbol, LocalDate fromDate, LocalDate toDate) {
        symbol = normalizeSymbol(symbol);
        cryptoValidationService.checkCryptoSupported(symbol);
        var from = LocalDateTimeUtils.getLocalDateTime(fromDate);
        var to = LocalDateTimeUtils.getLocalDateTime(toDate);
        var cryptoStatsDto = calculateCryptoStats(symbol, from, to);
        if (Objects.isNull(cryptoStatsRepository.findBySymbolAndFromDateAndToDate(symbol, cryptoStatsDto.getFromDate(), cryptoStatsDto.getToDate()))) {
            cryptoStatsRepository.save(cryptoStatsMapper.mapDtoToEntity(cryptoStatsDto));
        }
        return cryptoStatsDto;
    }

    @Override
    public CryptoNormalizedDTO getCryptoHighestNormalizedPrice(LocalDate date) {
        var symbols = cryptoRepository.getAllSymbols();

        if (CollectionUtils.isEmpty(symbols)) {
            log.info("Crypto symbols do not exist, please check that cryptos were loaded");
            return new CryptoNormalizedDTO();
        }

        var highestNormalizedPrice = BigDecimal.ZERO;
        var highestNormalizedSymbol = StringUtils.EMPTY;

        for (var symbol : symbols) {
            var normalizedCrypto = normalizeCryptoPrice(symbol, date, date.plusDays(1));
            if (normalizedCrypto.getNormalizedPrice().compareTo(highestNormalizedPrice) > 0) {
                highestNormalizedPrice = normalizedCrypto.getNormalizedPrice();
                highestNormalizedSymbol = normalizedCrypto.getSymbol();
            }
        }

        return CryptoNormalizedDTO.builder()
                .symbol(highestNormalizedSymbol)
                .normalizedPrice(highestNormalizedPrice)
                .build();
    }

    private CryptoNormalizedDTO normalizeCryptoPrice(String symbol, LocalDate fromDate, LocalDate toDate) {
        var cryptoStats = getCryptoStats(symbol, fromDate, toDate);
        var normalizedRange = new CryptoNormalizedDTO();
        normalizedRange.setSymbol(symbol);

        if (!BigDecimal.ZERO.equals(cryptoStats.getMin()) || !BigDecimal.ZERO.equals(cryptoStats.getMax())) {
            var normalizedPrice = (cryptoStats.getMax().subtract(cryptoStats.getMin()))
                    .divide(cryptoStats.getMin(), RoundingMode.HALF_EVEN);
            normalizedRange.setNormalizedPrice(normalizedPrice);
            return normalizedRange;
        }

        log.info("Can not calculate Crypto Normalized Price for {} Symbol, max or min prices do not exist", symbol);
        normalizedRange.setNormalizedPrice(BigDecimal.ZERO);
        return normalizedRange;
    }

    private String normalizeSymbol(String symbol) {
        return symbol.toUpperCase();
    }

    private void descendingSortOrderByPrice(List<CryptoNormalizedDTO> normalizedCryptosList) {
        normalizedCryptosList.sort(Comparator.comparing(CryptoNormalizedDTO::getNormalizedPrice).reversed());
    }

    private CryptoStatsDTO calculateCryptoStats(String symbol, LocalDateTime dateFrom, LocalDateTime dateTo) {
        var priceEntities = cryptoRepository.findBySymbol(symbol).getPrices();

        if (CollectionUtils.isEmpty(priceEntities)) {
            log.info("Crypto prices with this symbol: {} do not exist", symbol);
            return createCryptoStatsDTO(symbol, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    LocalDateTime.MIN, LocalDateTime.MAX);
        }

        var priceDTOs = cryptoMapper.mapDtoToEntity(priceEntities);

        if (Objects.nonNull(dateFrom) && Objects.nonNull(dateTo)) {
            priceDTOs = normalizePriceListByDataRange(priceDTOs, dateFrom.minusSeconds(1), dateTo);

            if (!CollectionUtils.isEmpty(priceDTOs)) {
                return createCryptoStatsDTO(symbol, priceDTOs.get(0).getPrice(), priceDTOs.get(priceDTOs.size() - 1).getPrice(),
                        calculateMinPrice(priceDTOs), calculateMaxPrice(priceDTOs), dateFrom, dateTo);
            }
        }

        if (priceEntities.size() != priceDTOs.size()) {
            log.info("Crypto prices with this symbol: {} and this data range: from {} and to{} do not exist", symbol, dateFrom, dateTo);
            return createCryptoStatsDTO(symbol, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    dateFrom, dateTo);
        }

        return createEntireTimePeriodCryptoStatsDTO(symbol, priceDTOs);
    }

    private List<CryptoPriceDTO> normalizePriceListByDataRange(List<CryptoPriceDTO> priceDTOs, LocalDateTime dateFrom, LocalDateTime dateTo) {
        return priceDTOs.stream()
                .filter(e -> e.getDateTime().isAfter(dateFrom.minusSeconds(1)) && e.getDateTime().isBefore(dateTo))
                .toList();
    }

    private CryptoStatsDTO createEntireTimePeriodCryptoStatsDTO(String symbol, List<CryptoPriceDTO> priceDTOs) {
        return createCryptoStatsDTO(symbol, priceDTOs.get(0).getPrice(), priceDTOs.get(priceDTOs.size() - 1).getPrice(),
                calculateMinPrice(priceDTOs), calculateMaxPrice(priceDTOs),
                priceDTOs.get(0).getDateTime(), priceDTOs.get(priceDTOs.size() - 1).getDateTime());
    }

    private CryptoStatsDTO createCryptoStatsDTO(String symbol, BigDecimal oldestPrice, BigDecimal newestPrice,
                                                BigDecimal min, BigDecimal max, LocalDateTime fromDate, LocalDateTime toDate) {
        return CryptoStatsDTO.builder()
                .symbol(symbol)
                .oldest(oldestPrice)
                .newest(newestPrice)
                .min(min)
                .max(max)
                .fromDate(fromDate)
                .toDate(toDate)
                .build();
    }

    private BigDecimal calculateMaxPrice(List<CryptoPriceDTO> prices) {
        return prices.stream()
                .map(CryptoPriceDTO::getPrice)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateMinPrice(List<CryptoPriceDTO> prices) {
        return prices.stream()
                .map(CryptoPriceDTO::getPrice)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
    }

}
