package com.sita.enterprises.TractorNow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;

@Configuration
public class PasswordResetConfig {

    @Bean
    public SecureRandom secureRandom() {
        return new SecureRandom();
    }
}
