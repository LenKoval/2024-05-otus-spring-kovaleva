package ru.otus.spring;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.spring.models.CoffeeBean;
import ru.otus.spring.models.CoffeeBeverage;
import ru.otus.spring.models.CoffeeCup;
import ru.otus.spring.models.CoffeeVariety;
import ru.otus.spring.services.CoffeeGateway;

import java.time.Duration;
import java.util.List;
import java.util.Arrays;
import java.util.Collection;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CoffeeIntegrationTest {

    @Autowired
    private CoffeeGateway coffeeGateway;

    private final static Logger logger = LoggerFactory.getLogger(CoffeeIntegrationTest.class);

    @Test
    public void testCoffeeProcessing() {
        CoffeeVariety arabica = CoffeeVariety.ARABICA;
        List<CoffeeBean> coffeeBeans = Arrays.asList(
                new CoffeeBean(100, arabica),
                new CoffeeBean(150, arabica)
        );

        Collection<CoffeeCup> processedCups = coffeeGateway.process(coffeeBeans);
        processedCups.forEach(cup -> logger.info("Processed cup: {}", cup.getBeverage()));

        await().atMost(Duration.ofSeconds(30))
                .until(() -> processedCups.size() == coffeeBeans.size());

        assertEquals(coffeeBeans.size(), processedCups.size());

        for (CoffeeCup cup : processedCups) {
            assertNotNull(cup.getVariety());
            assertNotNull(cup.getBeverage());
            assertTrue(cup.getBeverage() instanceof CoffeeBeverage);
        }
    }
}
