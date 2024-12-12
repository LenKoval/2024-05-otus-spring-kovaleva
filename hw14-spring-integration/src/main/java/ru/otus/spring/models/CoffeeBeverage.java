package ru.otus.spring.models;

//виды напитков
public enum CoffeeBeverage {

    ESPRESSO("Espresso"),
    AMERICANO("Americano"),
    CAPPUCCINO("Cappuccino");

    private final String name;

    CoffeeBeverage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
