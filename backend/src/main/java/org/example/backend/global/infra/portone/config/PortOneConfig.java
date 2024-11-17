package org.example.backend.global.infra.portone.config;

import com.siot.IamportRestClient.IamportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PortOneConfig {
    @Value("${imp.imp_key}")
    private String impKey;
    @Value("${imp.imp_secret}")
    private String impSecret;

    @Bean
    public IamportClient iamportClient() {
        return new IamportClient(impKey, impSecret);
    }
}
