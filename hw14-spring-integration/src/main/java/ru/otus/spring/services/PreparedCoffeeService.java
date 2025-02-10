package ru.otus.spring.services;

import ru.otus.spring.models.CoffeeCup;
import ru.otus.spring.models.GroundCoffee;

public interface PreparedCoffeeService {

    CoffeeCup preparedCoffee(GroundCoffee groundCoffee);
}
