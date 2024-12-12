package ru.otus.spring.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CoffeeBean {

    private int count;

    private CoffeeVariety variety;
}
