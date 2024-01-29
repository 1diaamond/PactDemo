package com.example.provider;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CardController {

    private final CardRepository repository;


    @Autowired
    CardController(CardRepository repository) {
        this.repository = repository;
    }

    @GetMapping("cards")
    List<Card> getAllCards() {
        return repository.fetchAll();
    }

    @GetMapping("cards/active")
    List<Card> getAllActiveCards() {
        return repository.fetchAllActive();
    }
}
