package com.xmcy.cryptorecommendation.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

/**
 * @param message Message with error information.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ErrorCustomResponseEntity(String message) {
}
