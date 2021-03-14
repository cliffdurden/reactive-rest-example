package com.github.cliffdurden.reactiverestexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.*;

import java.time.*;
import java.util.stream.Stream;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@SpringBootApplication
public class ReactiveRestServerExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveRestServerExampleApplication.class, args);
    }

    @Bean
    public RouterFunction<ServerResponse> route(GreetingService greetingService) {
        return RouterFunctions
                .route()
                .GET("/hello/{name}", r -> ok()
                        .contentType(MediaType.TEXT_PLAIN)
                        .body(greetingService.greet(r.pathVariable("name")), String.class))
                .GET("/hello_many/{name}", r -> ok()
                        .contentType(MediaType.TEXT_EVENT_STREAM)
                        .body(greetingService.greets(r.pathVariable("name")), String.class))
                .build();
    }
}

@Service
class GreetingService {

    Flux<String> greets(String name) {
        return Flux.fromStream(
                Stream.generate(
                        () -> "Hi, " + name + ". Current time is: " + Instant.now()))
                .delayElements(Duration.ofMillis(1000));
    }

    Mono<String> greet(String name) {
        return Mono.just("Hi, " + name + ". Current time is: " + Instant.now());
    }
}


