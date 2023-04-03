package com.xmcy.cryptorecommendation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CryptoInvestmentApp {

    public static void main(String[] args) {
        SpringApplication.run(CryptoInvestmentApp.class, args);
    }
}