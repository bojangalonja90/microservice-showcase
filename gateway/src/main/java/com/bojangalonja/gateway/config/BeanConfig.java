package com.bojangalonja.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Autowired
    private ApplicationConfigurationProperties applicationConfigurationProperties;

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/api/v1/first/**")
                        .filters(f -> f.rewritePath("/api/v1/first/(?<segment>.*)", "/${segment}")
                                .addRequestHeader("X-first-Header", "first-service-header"))
                        .uri(applicationConfigurationProperties.getApplicationAUrl())
                        .id("first-service"))

                .route(r -> r.path("/api/v1/second/**")
                        .filters(f -> f.rewritePath("/api/v1/second/(?<segment>.*)", "/${segment}")
                                .addRequestHeader("X-second-Header", "second-service-header"))
                        .uri(applicationConfigurationProperties.getApplicationBUrl())
                        .id("second-service"))
                .build();
    }

}
