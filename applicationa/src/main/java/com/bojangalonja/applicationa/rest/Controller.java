package com.bojangalonja.applicationa.rest;

import com.bojangalonja.applicationa.config.AppConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

@RestController
public class Controller {

    public static final Logger log = LoggerFactory.getLogger(Controller.class);

    @Autowired
    private AppConfigurationProperties appConfigurationProperties;

    @Autowired
    @Qualifier("applicationBWebClient")
    private WebClient applicationBWebClient;

    @RequestMapping(value = "/getstuff", method = RequestMethod.GET)
    public Mono<String> getFromB() {
        log.info("/getstuff called");


        return applicationBWebClient.get().retrieve()
                .bodyToMono(ResponseFromB.class)//
                .flatMap(r -> Mono.just("This is a response from b: returned to a: " + r.responseString));

    }

    @PostConstruct
    public void postConstruct() {
        log.info("post construct called!!!");
        String pass = appConfigurationProperties.getPassword();
        String username = appConfigurationProperties.getUsername();
        String url = appConfigurationProperties.getApplicationaUrl();
        log.info(String.format("user: %s, pass: %s, url: %s", username, pass, url));
    }

}

class ResponseFromB {
    String responseString;

    public ResponseFromB() {
    }

    public ResponseFromB(String responseString) {
        this.responseString = responseString;
    }

    public String getResponseString() {
        return responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }
}


