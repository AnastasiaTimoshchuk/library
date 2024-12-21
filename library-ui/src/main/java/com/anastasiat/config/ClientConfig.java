package com.anastasiat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Bean
    public RestClient libraryRestClient(
            @Value("${service.library-ui.uri}") String libraryUIBaseUrl
    ) {
        return RestClient.builder()
                .baseUrl(libraryUIBaseUrl)
                .build();
    }
}
