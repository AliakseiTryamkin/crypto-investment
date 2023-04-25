package com.xmcy.cryptorecommendation.repository;

import com.xmcy.cryptorecommendation.entity.CryptoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The repo interface for general Crypto operation
 */
@Repository
public interface CryptoRepository extends JpaRepository<CryptoEntity, Long> {

    CryptoEntity findBySymbol(String symbol);

    @Query(value = "SELECT symbol from CRYPTO_ENTITY", nativeQuery = true)
    List<String> getAllSymbols();

}
