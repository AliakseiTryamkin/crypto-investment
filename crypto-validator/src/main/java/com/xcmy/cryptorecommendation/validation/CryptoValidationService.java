package com.xcmy.cryptorecommendation.validation;

import com.xmcy.cryptorecommendation.dto.CryptoPriceDTO;

import java.util.List;

/**
 * Service for validate crypto data.
 */
public interface CryptoValidationService {

    /**
     * This method validates Crypto data for save process.
     */
    void validateSave(String symbol, List<CryptoPriceDTO> prices);

    /**
     * This method checks if Crypto exists or not, if not, a CryptoNotSupportedException exception will be returned.
     */
    void checkCryptoSupported(String symbol);
}
