package com.github.cliffdurden.reactiverestexample;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.*;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.*;

@SpringBootApplication
public class ReactiveRestClientExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveRestClientExampleApplication.class, args);
    }

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl("http://localhost:8080")
                .build();
    }
}

@Slf4j
@AllArgsConstructor
@Component
class ReactiveClient {

    private final WebClient webClient;

    public Mono<String> greet(String name) {
        return webClient
                .get()
                .uri("/hello/" + name)
                .accept(MediaType.TEXT_PLAIN)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorReturn("FAILED TO GET RESULT");
    }

    public Flux<String> greets(String name) {
        return webClient
                .get()
                .uri("/hello_many/" + name)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class)
                .onErrorReturn("FAILED TO GET RESULT");
    }
}

@Slf4j
@AllArgsConstructor
@Component
@Profile("!test")
class Initializer {

    private final ReactiveClient reactiveClient;

    @EventListener(ApplicationStartedEvent.class)
    public void onLoad() {
        reactiveClient.greet("Haruki")
                .subscribe(log::info);
        reactiveClient.greets("Midori")
                .subscribe(log::info);
    }
}


