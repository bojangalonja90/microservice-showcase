package com.bojangalonja.applicationa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfiguration {

    @Autowired
    private AppConfigurationProperties appConfigurationProperties;

    @Bean(name = "applicationBWebClient")
    public WebClient applicationBWebClient() {

        String url = appConfigurationProperties.getApplicationaUrl();
        WebClient wc = WebClient.builder().baseUrl(url+"/get")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)//
                .build();

        return wc;

    }

}
