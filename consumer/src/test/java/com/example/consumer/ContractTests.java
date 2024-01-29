package com.example.consumer;

import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonArray;
import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@ExtendWith(PactConsumerTestExt.class) //add Junit test extension for PACT
public class ContractTests {
    //define the contract
    @Pact(consumer = "cardsConsumer", provider = "cardsProvider") // define consumer and producer names between which a contract should be signed
    RequestResponsePact getAllCardsPact(PactDslWithProvider builder) { //pact method name will be used in contract test later
        return builder
                .given("cards exist") //state value will be used on producer side to clarify which what scenario is tested
                .uponReceiving("get all cards") // description is used on producer side when you have several tests with same state
                .path("/cards").method("GET")
                .willRespondWith().status(200)
                .body(PactDslJsonArray
                        .arrayMinLike(1)//min count of objects that should be in response
                        .id("id", 1L)
                        .stringType("cardType", "DEBIT") // define type of the field and value example that will be passed to test
                        .stringType("system", "visa")
                        .booleanType("active", true))
                .toPact();//finishing the contract
    }

    @Pact(consumer = "cardsConsumer", provider = "cardsProvider")
    RequestResponsePact getAllActiveCardsPact(PactDslWithProvider builder) {
        return builder
                .given("cards exist")
                .uponReceiving("get all active cards")
                .path("/cards/active")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(newJsonArray(array -> array.object(object -> {
                    object.id("id", 1L);
                    object.stringType("cardType", "DEBIT");
                    object.stringType("system", "visa");// define type of the field and value example that will be passed to test
                    object.booleanValue("active", true);// define type of the field and value that should be always returned in response
                }).object(object -> {
                    object.id("id", 2L);
                    object.stringType("cardType", "CREDIT");
                    object.stringType("system", "masterCard");
                    object.booleanValue("active", true);
                })).build()).toPact();
    }

    @Pact(consumer = "cardsConsumer", provider = "cardsProvider")
    RequestResponsePact noExistingCardsPact(PactDslWithProvider builder) {
        return builder
                .given("cards do not exist")
                .uponReceiving("get all cards")
                .method("GET")
                .path("/cards")
                .willRespondWith()
                .status(200)
                .headers(Map.of())
                .body("[]")
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "getAllCardsPact")
    public void getAllCardsPactTest(MockServer mockServer) {
        var cardVisa = new Card(1, "DEBIT", "visa", true);
        var expectedResult = List.of(cardVisa);

        var cardService = new CardService(getRestTemplate(mockServer));
        var actualResult = cardService.getAllCards();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    @PactTestFor(pactMethod = "getAllActiveCardsPact")
    public void getAllActiveCardsPactTest(MockServer mockServer) {
        Card cardVisa = new Card(1, "DEBIT", "visa", true);
        Card cardMaster = new Card(2, "CREDIT", "masterCard", true);
        var expectedResult = List.of(cardVisa, cardMaster);

        var cardService = new CardService(getRestTemplate(mockServer));
        var actualResult = cardService.getAllActiveCards();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    @PactTestFor(pactMethod = "noExistingCardsPact", port = "9999")
    public void getAllCardsWhenNoCardsExistTest(MockServer mockServer) {
        var cardService = new CardService(getRestTemplate(mockServer));
        var actualResult = cardService.getAllCards();
        assertEquals(Collections.emptyList(), actualResult);
    }

    private RestTemplate getRestTemplate(MockServer mockServer) {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        var restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        restTemplate.setMessageConverters(messageConverters);
        return restTemplate;
    }

}