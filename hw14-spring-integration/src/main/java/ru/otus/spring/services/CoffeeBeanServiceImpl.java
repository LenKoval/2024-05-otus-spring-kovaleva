package ru.otus.spring.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.spring.models.CoffeeBean;
import ru.otus.spring.models.CoffeeVariety;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;

@Service
@RequiredArgsConstructor
public class CoffeeBeanServiceImpl implements CoffeeBeanService {

    private final RandomGenerator randomGenerator = RandomGenerator.getDefault();

    @Override
    public List<CoffeeBean> generateCoffeeBeanList() {
        int listSize = randomGenerator.nextInt(10, 100);
        List<CoffeeBean> coffeeBeanList = new ArrayList<>(listSize);
        for (int i = 0; i < listSize; i++) {
            coffeeBeanList.add(generateCoffeeBean());
        }
        return coffeeBeanList;
    }

    private CoffeeBean generateCoffeeBean() {
        int count = randomGenerator.nextInt(50, 100);
        CoffeeVariety variety = CoffeeVariety.values()[new Random().nextInt(CoffeeVariety.values().length)];
        return new CoffeeBean(count, variety);
    }
}
