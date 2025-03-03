package ru.otus.spring.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.spring.models.CoffeeCup;
import ru.otus.spring.models.GroundCoffee;
import ru.otus.spring.models.CoffeeBeverage;

import java.util.Random;

@Service
@Slf4j
public class PreparedCoffeeServiceImpl implements PreparedCoffeeService {

    @Override
    public CoffeeCup preparedCoffee(GroundCoffee groundCoffee) {
        log.info("Prepared {} coffee bean start", groundCoffee.getCoffeeVariety().getName());
        delay();
        log.info("Prepared {} coffee bean done", groundCoffee.getCoffeeVariety().getName());
        return new CoffeeCup(groundCoffee.getCoffeeVariety(), generateCoffeeBeverage());
    }

    private static void delay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static CoffeeBeverage generateCoffeeBeverage() {
        CoffeeBeverage[] beverages = CoffeeBeverage.values();
        Random random = new Random();
        CoffeeBeverage randomBeverage = beverages[random.nextInt(beverages.length)];

        return randomBeverage;
    }
}
