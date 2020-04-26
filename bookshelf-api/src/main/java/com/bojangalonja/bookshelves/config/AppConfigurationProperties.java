package com.bojangalonja.bookshelves.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfigurationProperties {

    @NotBlank
    private String volumesApiUrl;

    public String getVolumesApiUrl() {
        return volumesApiUrl;
    }

    public void setVolumesApiUrl(String volumesApiUrl) {
        this.volumesApiUrl = volumesApiUrl;
    }
}
