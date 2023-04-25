package com.xcmy.cryptorecommendation.dataloader.service;

import com.xmcy.cryptorecommendation.dto.CryptoPriceDTO;

import java.util.List;

public interface CryptoDataLoaderService {

    /**
     * The method loads Crypto data from CSV files and store it.
     */
    List<String> loadAndSaveCryptoData();

    /**
     * The method saves Crypto data.
     */
    void saveCrypto(String symbol, List<CryptoPriceDTO> pricesDto);
}
