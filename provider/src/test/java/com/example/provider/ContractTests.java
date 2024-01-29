package com.example.provider;

import static org.mockito.Mockito.when;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@PactFolder("pacts") //set directory for the PACT file
@Provider("cardsProvider") // set provider name that you used  in contract
@ExtendWith(SpringExtension.class) //add PACT extension for JUnit
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContractTests {

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class) //add PACT extension for TestTemplate
    void verifyPact(PactVerificationContext context) {
        context.verifyInteraction();//called to verify the interaction from the test template method.
    }

    @LocalServerPort
    int port;

    @MockBean
    private CardController cardController;

    @BeforeEach
    void setUp(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", port));
    }

    @State({"get all cards", "cards exist"}) // equals to given on consumer side, and if several pacts have same given , the description fom pact should be added
    void testGetAllCards() {
        Card cardVisa = new Card(1, "DEBIT", "visa", true);
        Card cardMaster = new Card(2, "CREDIT", "masterCard", false);
        when(cardController.getAllCards()).thenReturn(List.of(
                cardVisa, cardMaster));
    }

    @State({"get all active", "cards exist"})
    void testGetAllActiveCards() {
        Card cardVisa = new Card(1, "DEBIT", "visa", true);
        Card cardMaster = new Card(2, "CREDIT", "masterCard", true);
        var expectedResult = List.of(cardVisa, cardMaster);
        when(cardController.getAllActiveCards()).thenReturn(expectedResult);
    }

    @State("cards do not exist")
    void testGetCardsNotExist() {
        when(cardController.getAllCards()).thenReturn(Collections.emptyList());
    }
}
