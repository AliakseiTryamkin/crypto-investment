package com.xmcy.cryptorecommendation.mapper;

import com.xmcy.cryptorecommendation.dto.CryptoDTO;
import com.xmcy.cryptorecommendation.dto.CryptoPriceDTO;
import com.xmcy.cryptorecommendation.entity.CryptoEntity;
import com.xmcy.cryptorecommendation.entity.CryptoPriceEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface CryptoMapper {

    CryptoEntity mapDtoToEntity(CryptoDTO cryptoDTO);
    CryptoPriceEntity mapDtoToEntity(CryptoPriceDTO cryptoDTO);

    List<CryptoPriceDTO> mapDtoToEntity(List<CryptoPriceEntity> cryptoPriceEntities);
}
