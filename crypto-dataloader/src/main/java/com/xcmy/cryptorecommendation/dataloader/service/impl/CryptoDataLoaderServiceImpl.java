package com.xcmy.cryptorecommendation.dataloader.service.impl;

import com.xcmy.cryptorecommendation.dataloader.service.CryptoDataLoaderService;
import com.xcmy.cryptorecommendation.service.CryptoRecommendationService;
import com.xcmy.cryptorecommendation.validation.CryptoValidationService;
import com.xmcy.cryptorecommendation.dto.CryptoPriceDTO;
import com.xmcy.cryptorecommendation.entity.CryptoEntity;
import com.xmcy.cryptorecommendation.entity.CryptoPriceEntity;
import com.xmcy.cryptorecommendation.mapper.CryptoMapper;
import com.xmcy.cryptorecommendation.repository.CryptoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CryptoDataLoaderServiceImpl implements CryptoDataLoaderService {

    private static final String FILE_SUFFIX = "_values.csv";
    private static final String CRYPTO_TIMESTAMP_COLUMN = "timestamp";
    private static final String CRYPTO_SYMBOL_COLUMN = "symbol";
    private static final String CRYPTO_PRICE_COLUMN = "price";

    @Value("${files.input}")
    private String cryptoFilesPath;

    private final CryptoRecommendationService cryptoRecommendationService;

    private final CryptoValidationService cryptoValidationService;

    private final CryptoMapper cryptoMapper;

    private final CryptoRepository cryptoRepository;

    @Override
    public List<String> loadAndSaveCryptoData() {
        log.info("Crypto data 'load' process has been started");

        Map<String, List<CryptoPriceDTO>> cryptoData;
        try {
            cryptoData = loadCryptoData(cryptoFilesPath);
        } catch (IOException e) {
            //TODO add some custom exception and remove emptyList here
            log.error("Crypto data did not loaded", e);
            return Collections.emptyList();
        }
        log.info("Crypto data 'load' process has been completed: {}", cryptoData.keySet());

        cryptoData.forEach(this::saveCrypto);
        return Collections.emptyList();
    }

    @Caching(evict = {
            @CacheEvict(value = "crypto", allEntries = true),
            @CacheEvict(value = "cryptos", allEntries = true)})
    @Override
    public void saveCrypto(String symbol, List<CryptoPriceDTO> pricesDto) {
        symbol = normalizeSymbol(symbol);
        cryptoValidationService.validateSave(symbol, pricesDto);

        List<CryptoPriceEntity> pricesEntities = pricesDto.stream()
                .map(cryptoMapper::mapDtoToEntity)
                .sorted(Comparator.comparing(CryptoPriceEntity::getDateTime))
                .collect(Collectors.toList());

        cryptoRepository.save(createCryptoEntity(symbol, pricesEntities));
        cryptoRecommendationService.saveCryptoStats(symbol, pricesEntities.get(0).getDateTime(),
                pricesEntities.get(pricesEntities.size() - 1).getDateTime());
    }

    private String normalizeSymbol(String symbol) {
        return symbol.toUpperCase();
    }

    private Map<String, List<CryptoPriceDTO>> loadCryptoData(String path) throws IOException {
        var cryptoDataMap = new HashMap<String, List<CryptoPriceDTO>>();
        File cryptoFileDirectory = ResourceUtils.getFile(path);
        var cryptoFiles = FileUtils.listFiles(cryptoFileDirectory,
                        FileFilterUtils.suffixFileFilter(FILE_SUFFIX), null)
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        for (var cryptoFile : cryptoFiles) {
            var symbol = StringUtils.removeEnd(cryptoFile.getName(), FILE_SUFFIX);
            var prices = loadCryptoPricesFromCsvFile(cryptoFile.getAbsolutePath());
            cryptoDataMap.put(symbol, prices);
        }

        return cryptoDataMap;
    }

    private List<CryptoPriceDTO> loadCryptoPricesFromCsvFile(String filePath) throws IOException {
        var prices = new ArrayList<CryptoPriceDTO>();
        var reader = Files.newBufferedReader(Paths.get(filePath));

        try (reader) {
            var csvRecords = CSVFormat.DEFAULT.builder()
                    .setHeader(CRYPTO_TIMESTAMP_COLUMN, CRYPTO_SYMBOL_COLUMN, CRYPTO_PRICE_COLUMN)
                    .setSkipHeaderRecord(true).build()
                    .parse(reader);

            for (var csvRecord : csvRecords) {
                Timestamp timestamp = parseTimestamp(csvRecord.get(CRYPTO_TIMESTAMP_COLUMN));
                var price = Double.parseDouble(csvRecord.get(CRYPTO_PRICE_COLUMN));

                var priceDTO = new CryptoPriceDTO();
                priceDTO.setPrice(BigDecimal.valueOf(price));
                if (Objects.nonNull(timestamp)) {
                    priceDTO.setDateTime(timestamp.toLocalDateTime());
                }
                prices.add(priceDTO);
            }
        }
        return prices;
    }

    private CryptoEntity createCryptoEntity(String symbol, List<CryptoPriceEntity> pricesEntities) {
        return CryptoEntity.builder()
                .symbol(symbol)
                .prices(pricesEntities)
                .build();
    }

    public static Timestamp parseTimestamp(String milliSecTimestamp) {
        return StringUtils.isNotEmpty(milliSecTimestamp) ? new Timestamp(Long.parseLong(milliSecTimestamp)) : null;
    }
}
