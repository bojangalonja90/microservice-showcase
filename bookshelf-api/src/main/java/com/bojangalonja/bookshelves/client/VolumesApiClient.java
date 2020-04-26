package com.bojangalonja.bookshelves.client;

import com.bojangalonja.bookshelves.config.AppConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class VolumesApiClient {

    public static final Logger LOG = LoggerFactory.getLogger(VolumesApiClient.class);

    private final AppConfigurationProperties appConfigurationProperties;

    private final WebClient webClient;

    public VolumesApiClient(WebClient webClient, AppConfigurationProperties appConfigurationProperties) {
        this.webClient = webClient;
        this.appConfigurationProperties = appConfigurationProperties;
    }

    public Mono<Volume> getVolume(String id) {
       return webClient.get().uri(appConfigurationProperties.getVolumesApiUrl() + "/volumes/" + id)
                .retrieve().bodyToMono(Volume.class);
    }

}
