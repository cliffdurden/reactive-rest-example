package com.github.cliffdurden.reactiverestexample;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class ReactiveRestServerExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveRestServerExampleApplication.class, args);
    }

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl("http://localhost:8080")
                .build();
    }
}

@Slf4j
@AllArgsConstructor
@Component
class ReactiveClient {

    private final WebClient webClient;

    @EventListener(ApplicationStartedEvent.class)
    public void onLoad() {
        webClient
                .get()
                .uri("/hello_many/haruki")
                .accept(MediaType.TEXT_PLAIN)
                .retrieve()
                .bodyToFlux(String.class)
                .onErrorReturn("FAILED TO GET RESULT")
                .subscribe(log::info);
    }
}


