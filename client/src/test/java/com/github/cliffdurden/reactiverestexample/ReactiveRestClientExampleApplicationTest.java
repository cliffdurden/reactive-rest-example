package com.github.cliffdurden.reactiverestexample;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import groovy.util.logging.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.spec.internal.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.*;
import reactor.core.publisher.*;
import reactor.test.StepVerifier;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {WireMockInitializer.class})

@Slf4j
class ReactiveRestClientExampleApplicationTest {

    @Autowired
    private ReactiveClient testSubject;

    @Autowired
    private WireMockServer wireMockServer;

    @AfterEach
    public void afterEach() {
        this.wireMockServer.resetAll();
    }

    @DisplayName("Should return correct single result from server")
    @Test
    void testGreet() {
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/hello/testName"))
                .willReturn(
                        WireMock.aResponse()
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                                .withBody("Hi, testName.")
                                .withStatus(HttpStatus.OK)
                ));

        Mono<String> result = testSubject.greet("testName");

        StepVerifier
                .create(result)
                .expectNext("Hi, testName.")
                .expectComplete()
                .verify();
    }


    @DisplayName("Should return correct results from server")
    @Test
    void testGreets() {
        var eventStream = """
                Hi, testName. 1
                Hi, testName. 2
                Hi, testName. 3
                """;

        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/hello_many/testName"))
                .willReturn(
                        WireMock.aResponse()
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                                .withBody(eventStream)
                                .withStatus(HttpStatus.OK)

                ));

        Flux<String> result = testSubject.greets("testName");

        StepVerifier
                .create(result)
                .expectNext("Hi, testName. 1")
                .expectNext("Hi, testName. 2")
                .expectNext("Hi, testName. 3")
                .expectComplete()
                .verify();
    }
}

