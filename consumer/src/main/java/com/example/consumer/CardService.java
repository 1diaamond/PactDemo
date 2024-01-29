package com.example.consumer;

import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CardService {

    private final RestTemplate restTemplate;

    public CardService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Card> getAllCards() {
        return restTemplate.exchange("/cards", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<List<Card>>() {}).getBody();
    }

    public List<Card> getAllActiveCards() {
        return restTemplate.exchange("/cards/active", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<List<Card>>() {
                }).getBody();
    }
}
