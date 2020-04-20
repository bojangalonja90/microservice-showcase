package com.bojangalonja.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gateway")
public class ApplicationConfigurationProperties {


    private String volumesApiUrl;

    private String bookshelvesApiUrl;

    public String getBookshelvesApiUrl() {
        return bookshelvesApiUrl;
    }

    public void setBookshelvesApiUrl(String bookshelvesApiUrl) {
        this.bookshelvesApiUrl = bookshelvesApiUrl;
    }

    public String getVolumesApiUrl() {
        return volumesApiUrl;
    }

    public void setVolumesApiUrl(String volumesApiUrl) {
        this.volumesApiUrl = volumesApiUrl;
    }
}
