package com.xcmy.cryptorecommendation.dataloader.controller;

import com.xcmy.cryptorecommendation.dataloader.service.CryptoDataLoaderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping(path = "v1/cryptos/data")
@AllArgsConstructor
public class CryptoDataLoaderController {

    private final CryptoDataLoaderService cryptoDataLoaderService;

    @Operation(summary = "Load Crypto data and store it to data storage.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All Crypto data were loaded.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "404", description = "Crypto data did not loaded.")})
    @PostMapping("/load")
    public ResponseEntity<List<String>> loadCryptoData() {
        return ResponseEntity.ok(cryptoDataLoaderService.loadAndSaveCryptoData());
    }

}
