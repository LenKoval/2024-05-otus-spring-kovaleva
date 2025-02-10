package ru.otus.spring.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CoffeeCup {

    private CoffeeVariety variety;

    private CoffeeBeverage beverage;

}
