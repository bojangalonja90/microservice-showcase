package com.bojangalonja.applicationb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;



@RestController
public class Controller {

    public static final Logger log = LoggerFactory.getLogger(Controller.class);

    @GetMapping(value = "/get")
    public Mono<ResponseFromB> getFromB() throws InterruptedException {
        log.info("B is called");
        Thread.sleep(2000L);
        ResponseFromB response = new ResponseFromB("Hi from b");

        return Mono.just(response);
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
}


