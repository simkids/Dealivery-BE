package org.example.backend.global.infra.network.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
	@Value("${domain}")
	private String domain;

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder
			.rootUri(domain)
			.build();
	}
}
