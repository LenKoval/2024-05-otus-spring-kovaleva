package ru.otus.spring.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class GroundCoffee {

    private int count;

    private CoffeeVariety coffeeVariety;
}
