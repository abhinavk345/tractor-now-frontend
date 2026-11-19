package com.sita.enterprises.TractorNow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Msg91Config {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}