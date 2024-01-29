package com.example.provider;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class CardRepository {

    private static final List<Card> CARDS = new ArrayList<>() {{
        add(new Card(1, "DEBIT", "visa", true));
        add(new Card(2, "CREDIT", "masterCard", false));
        add(new Card(3, "CREDIT", "maestro", true));
        add(new Card(3, "CREDIT", "bitCard", false));
    }};

    public List<Card> fetchAll() {
        return CARDS;
    }

    public List<Card> fetchAllActive() {
        return CARDS.stream().filter(Card::isActive).toList();
    }

}
