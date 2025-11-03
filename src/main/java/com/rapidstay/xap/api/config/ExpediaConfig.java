package com.rapidstay.xap.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExpediaConfig {

    @Value("${expedia.api.key:dummy-key}")
    private String apiKey;

    @Value("${expedia.api.url:https://test.api.expediapartnersolutions.com}")
    private String baseUrl;

    public String getApiKey() {
        return apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
