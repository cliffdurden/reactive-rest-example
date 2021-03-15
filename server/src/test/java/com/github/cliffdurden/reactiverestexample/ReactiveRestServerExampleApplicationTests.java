package com.github.cliffdurden.reactiverestexample;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.*;
import reactor.test.StepVerifier;

import static org.mockito.BDDMockito.given;

@WebFluxTest
@ExtendWith(SpringExtension.class)
class ReactiveRestServerExampleApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GreetingService dummyGreetingService;

    @BeforeEach
    public void setUp() {
        given(dummyGreetingService.greet("testName"))
                .willReturn(Mono.just("Hi, testName."));

        given(dummyGreetingService.greets("testName"))
                .willReturn(Flux.just(
                        "Hi, testName. 1",
                        "Hi, testName. 2",
                        "Hi, testName. 3"));
    }

    @DisplayName("Should return one correct value")
    @Test
    void testHello() {
        webTestClient
                .get()
                .uri("/hello/testName")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_PLAIN)
                .expectBody(String.class)
                .isEqualTo("Hi, testName.");
    }

    @DisplayName("Should return many correct values")
    @Test
    void testHelloMany() {
        Flux<String> results = webTestClient
                .get()
                .uri("/hello_many/testName")
                .exchange()
                .returnResult(String.class).getResponseBody();

        StepVerifier.create(results.log())
                .expectNext("Hi, testName. 1")
                .expectNext("Hi, testName. 2")
                .expectNext("Hi, testName. 3")
                .expectComplete()
                .verify();
    }

}
