package com.bojangalonja.applicationa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfigurationProperties {

    private String username;
    private String password;
    private String applicationaUrl;
    private String message = "a message that can be changed live";

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getApplicationaUrl() {
        return applicationaUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setApplicationaUrl(String applicationaUrl) {
        this.applicationaUrl = applicationaUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
