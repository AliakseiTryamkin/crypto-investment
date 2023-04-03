package com.xmcy.cryptorecommendation.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;

@NoArgsConstructor
@Getter
public class DevCustomCryptoException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private ExceptionCode exceptionCode;

    public DevCustomCryptoException(ExceptionCode exceptionCode) {
        super();
        this.exceptionCode = exceptionCode;
    }

    public DevCustomCryptoException(String message, Throwable cause, ExceptionCode exceptionCode) {
        super(message, cause);
        this.exceptionCode = exceptionCode;
    }

    public DevCustomCryptoException(String message, ExceptionCode exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

    public DevCustomCryptoException(Throwable cause, ExceptionCode exceptionCode) {
        super(cause);
        this.exceptionCode = exceptionCode;
    }

}
