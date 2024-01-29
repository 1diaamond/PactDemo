package com.example.consumer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Card {

    private int id;
    private String cardType;
    private String system;
    private boolean active;
}
