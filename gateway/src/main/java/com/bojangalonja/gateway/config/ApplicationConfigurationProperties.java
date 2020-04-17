package com.bojangalonja.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gateway")
public class ApplicationConfigurationProperties {


    private String volumesApiUrl;

    private String applicationBUrl;

    public String getApplicationBUrl() {
        return applicationBUrl;
    }

    public void setApplicationBUrl(String applicationBUrl) {
        this.applicationBUrl = applicationBUrl;
    }

    public String getVolumesApiUrl() {
        return volumesApiUrl;
    }

    public void setVolumesApiUrl(String volumesApiUrl) {
        this.volumesApiUrl = volumesApiUrl;
    }
}
