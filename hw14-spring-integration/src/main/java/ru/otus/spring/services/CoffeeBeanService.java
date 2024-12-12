package ru.otus.spring.services;

import ru.otus.spring.models.CoffeeBean;

import java.util.List;

public interface CoffeeBeanService {

    List<CoffeeBean> generateCoffeeBeanList();
}
