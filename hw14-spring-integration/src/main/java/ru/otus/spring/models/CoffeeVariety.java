package ru.otus.spring.models;

//сорт кофе
public enum CoffeeVariety {
    ARABICA("Arabica"),
    ROBUSTA("Robusta"),
    EXCELSA("Excelsa"),
    LIBERICA("Liberica");

    private final String name;

    CoffeeVariety(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
