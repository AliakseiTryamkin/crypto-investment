package com.xmcy.cryptorecommendation.service.impl;

import com.xmcy.cryptorecommendation.dto.CryptoPriceDTO;
import com.xmcy.cryptorecommendation.entity.CryptoEntity;
import com.xmcy.cryptorecommendation.entity.CryptoPriceEntity;
import com.xmcy.cryptorecommendation.mapper.CryptoMapper;
import com.xmcy.cryptorecommendation.repository.CryptoRepository;
import com.xmcy.cryptorecommendation.service.CryptoRecommendationService;
import com.xmcy.cryptorecommendation.service.CryptoService;
import com.xmcy.cryptorecommendation.service.CryptoValidationService;
import com.xmcy.cryptorecommendation.util.TimestampUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CryptoServiceImpl implements CryptoService {

    private static final String FILE_DIR_INPUT = "src/main/resources/input";

    private static final String FILE_SUFFIX = "_values.csv";
    private static final String CRYPTO_TIMESTAMP_COLUMN = "timestamp";
    private static final String CRYPTO_SYMBOL_COLUMN = "symbol";
    private static final String CRYPTO_PRICE_COLUMN = "price";


    @Value("${files.input}")
    private String cryptoFilesPath;

    private final ResourceLoader resourceLoader;
    private final CryptoValidationService cryptoValidationService;
    private final CryptoRecommendationService cryptoRecommendationService;
    private final CryptoMapper cryptoMapper;
    private final CryptoRepository cryptoRepository;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "crypto", allEntries = true),
            @CacheEvict(value = "cryptos", allEntries = true)})
    public void saveCrypto(String symbol, List<CryptoPriceDTO> pricesDto) {
        symbol = normalizeSymbol(symbol);
        cryptoValidationService.validateSave(symbol, pricesDto);

        List<CryptoPriceEntity> pricesEntities = pricesDto.stream()
                .map(cryptoMapper::mapDtoToEntity)
                .sorted(Comparator.comparing(CryptoPriceEntity::getDateTime))
                .toList();

        cryptoRepository.save(createCryptoEntity(symbol, pricesEntities));
        cryptoRecommendationService.saveCryptoStats(symbol, pricesEntities.get(0).getDateTime(),
                 pricesEntities.get(pricesEntities.size() - 1).getDateTime());
    }

    @Override
    public void loadAndSaveCryptos() {
        log.info("Crypto data 'download' started");

        Map<String, List<CryptoPriceDTO>> cryptoData;
        try {
            cryptoData = loadCryptoData(cryptoFilesPath);
        } catch (IOException e) {
            //TODO add some custom exception
            log.error("Crypto data not downloaded", e);
            return;
        }
        log.info("Crypto data 'download' completed: {}", cryptoData.keySet());

        cryptoData.forEach(this::saveCrypto);
    }

    private String normalizeSymbol(String symbol) {
        return symbol.toUpperCase();
    }

    private Map<String, List<CryptoPriceDTO>> loadCryptoData(String path) throws IOException {
        var cryptoData = new HashMap<String, List<CryptoPriceDTO>>();
        var cryptoFileDirectory = ResourceUtils.getFile(path);
        var cryptoFiles = FileUtils.listFiles(cryptoFileDirectory,
                        FileFilterUtils.suffixFileFilter(FILE_SUFFIX), null)
                .stream()
                .filter(Objects::nonNull)
                .toList();

        for (var cryptoFile : cryptoFiles) {
            var symbol = StringUtils.removeEnd(cryptoFile.getName(), FILE_SUFFIX);
            var prices = loadCryptoPricesFromCsvFile(cryptoFile.getAbsolutePath());
            cryptoData.put(symbol, prices);
        }

        return cryptoData;
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
                var timestamp = TimestampUtils.parseTimestamp(csvRecord.get(CRYPTO_TIMESTAMP_COLUMN));
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

}
