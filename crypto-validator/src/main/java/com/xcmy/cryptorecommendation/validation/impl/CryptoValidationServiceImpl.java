package com.xcmy.cryptorecommendation.validation.impl;

import com.xcmy.cryptorecommendation.validation.CryptoValidationService;
import com.xmcy.cryptorecommendation.dto.CryptoPriceDTO;
import com.xmcy.cryptorecommendation.exception.DevCustomCryptoException;
import com.xmcy.cryptorecommendation.exception.ExceptionCode;
import com.xmcy.cryptorecommendation.repository.CryptoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class CryptoValidationServiceImpl implements CryptoValidationService {

    private final CryptoRepository cryptoRepository;

    @Override
    public void validateSave(String symbol, List<CryptoPriceDTO> prices) {
        if (StringUtils.isBlank(symbol)) {
            throw new DevCustomCryptoException("Crypto symbol cannot be empty.", ExceptionCode.CRYPTO_SYMBOL_EMPTY);
        }

        if (CollectionUtils.isEmpty(prices)) {
            throw new DevCustomCryptoException("Crypto price list cannot be empty.", ExceptionCode.CRYPTO_PRICE_EMPTY);
        }
    }

    @Override
    public void checkCryptoSupported(String symbol) {
        var cryptoEntity = cryptoRepository.findBySymbol(symbol);
        if (Objects.isNull(cryptoEntity)) {
            throw new DevCustomCryptoException("This crypto type is not supported: " + symbol, ExceptionCode.CRYPTO_TYPE_NOT_SUPPORTED);
        }
    }

}
