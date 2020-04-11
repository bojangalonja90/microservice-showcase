package com.bojangalonja.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gateway")
public class ApplicationConfigurationProperties {

    private String applicationAUrl;

    private String applicationBUrl;

    public String getApplicationAUrl() {
        return applicationAUrl;
    }

    public void setApplicationAUrl(String applicationAUrl) {
        this.applicationAUrl = applicationAUrl;
    }

    public String getApplicationBUrl() {
        return applicationBUrl;
    }

    public void setApplicationBUrl(String applicationBUrl) {
        this.applicationBUrl = applicationBUrl;
    }
}
