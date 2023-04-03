package com.xmcy.cryptorecommendation.service;

import com.xmcy.cryptorecommendation.dto.CryptoPriceDTO;
import com.xmcy.cryptorecommendation.entity.CryptoEntity;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.List;

/**
 * Service for processing crypto data.
 */
public interface CryptoService {

    /**
     * The method saves Crypto data.
     */
    void saveCrypto(String symbol, List<CryptoPriceDTO> prices);

    /**
     * The method loads Crypto data from CSV files on ApplicationReadyEvent and store it.
     */
    @EventListener(ApplicationReadyEvent.class)
    void loadAndSaveCryptos();

}
