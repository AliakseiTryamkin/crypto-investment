package com.xmcy.cryptorecommendation.repository;

import com.xmcy.cryptorecommendation.entity.CryptoStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface CryptoStatsRepository extends JpaRepository<CryptoStatsEntity, Long> {

    CryptoStatsEntity findBySymbolAndFromDateAndToDate(String symbol, LocalDateTime fromDate, LocalDateTime toDate);
}
