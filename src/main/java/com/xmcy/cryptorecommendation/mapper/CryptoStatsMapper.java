package com.xmcy.cryptorecommendation.mapper;

import com.xmcy.cryptorecommendation.dto.CryptoStatsDTO;
import com.xmcy.cryptorecommendation.entity.CryptoStatsEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface CryptoStatsMapper {

    CryptoStatsEntity mapDtoToEntity(CryptoStatsDTO cryptoStatsDTO);
}
