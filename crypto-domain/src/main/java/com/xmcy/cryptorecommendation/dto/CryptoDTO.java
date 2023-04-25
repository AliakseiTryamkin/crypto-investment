package com.xmcy.cryptorecommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CryptoDTO {

    private String symbol;
    private List<CryptoPriceDTO> prices;
}
