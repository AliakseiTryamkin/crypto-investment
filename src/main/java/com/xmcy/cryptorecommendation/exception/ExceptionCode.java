package com.xmcy.cryptorecommendation.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum ExceptionCode {

    CRYPTO_TYPE_NOT_SUPPORTED(1, "The Crypto type is not supported."),
    CRYPTO_SYMBOL_EMPTY(2, "Crypto symbol cannot be empty."),
    CRYPTO_PRICE_EMPTY(3, "Crypto price list cannot be empty.");

    private int code;
    private String message;
}
