package com.bojangalonja.gateway.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class BeanConfig {

    @Autowired
    private ApplicationConfigurationProperties applicationConfigurationProperties;

    @Autowired
    RouteDefinitionLocator locator;


    /**
     * Names of the service routes needs to follow the pattern <service-name>-service, where
     * service suffix will be stripped. This is needed in order for the open-api documentation
     * to be processed in {@link BeanConfig#openApis(org.springframework.cloud.gateway.route.RouteLocator)}
     *
     * @param builder
     * @return
     */
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/api/v1/volumes/**")
                        .filters(f -> f.rewritePath("/api/v1/volumes/(?<segment>.*)", "/${segment}"))
                        .uri(applicationConfigurationProperties.getVolumesApiUrl())
                        .id("volumes-service"))
                .route(r -> r.path("/api/v1/bookshelves/**")
                        .filters(f -> f.rewritePath("/api/v1/bookshelves/(?<segment>.*)", "/${segment}"))
                        .uri(applicationConfigurationProperties.getBookshelvesApiUrl())
                        .id("bookshelves-service"))
                .route(r -> r.path("/v3/api-docs/**")
                        .filters(f -> f.rewritePath("/v3/api-docs/(?<path>.*)", "/api/v1/${path}/v3/api-docs"))
                        .uri("http://localhost:8080")
                        .id("open-api"))
                .build();
    }


    @Bean
    @Autowired
    public List<GroupedOpenApi> openApis(RouteLocator locator) {
        List<GroupedOpenApi> groups = new ArrayList<>();
        List<Route> routes = locator.getRoutes().collectList().block();
        routes.stream().filter(r -> r.getId().matches(".*-service")).forEach(route -> {
            String name = route.getId().replaceAll("-service", "");
            GroupedOpenApi g = GroupedOpenApi.builder().pathsToMatch("/" + name + "/**").setGroup(name).build();
            groups.add(g);
        });
        return groups;
    }
}
