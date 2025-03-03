package ru.otus.spring.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.spring.models.CoffeeCup;
import ru.otus.spring.models.CoffeeBean;
import ru.otus.spring.models.CoffeeVariety;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

@Service
@RequiredArgsConstructor
@Slf4j
public class CafeServiceImpl implements CafeService {

    private final CoffeeGateway coffeeGateway;

    @Override
    public void startPreparedCoffeeCups() {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        for (int i = 0; i < 10; i++) {
            int num = i + 1;
            pool.execute(() -> {
                CoffeeVariety coffeeVariety = generateCoffeeVariety();
                Collection<CoffeeBean> coffeeBeans = generateCoffeeBeans(coffeeVariety);
                log.info("{}, Start process coffee beans, count: {}, variety: {}",
                        num, coffeeBeans.size(), coffeeBeans.stream()
                                .map(bean -> bean.getCount()), coffeeVariety.getName());

                Collection<CoffeeCup> coffeeCups = coffeeGateway.process(coffeeBeans);

                if (coffeeCups == null) {
                    log.info("{}, End process, not a single cup prepared from {} coffee beans {}",
                            num, coffeeBeans.size(), coffeeVariety.getName());
                } else {
                    log.info("{}, End process, {} coffee cups prepared from coffee bean {}",
                            num, coffeeCups.size(), coffeeBeans.size(), coffeeVariety.getName(), coffeeCups.stream()
                                    .map(cup -> cup.getBeverage().getName()));
                }
            });
            delay();
        }
    }

    private static CoffeeVariety generateCoffeeVariety() {
        CoffeeVariety[] coffeeVarieties = CoffeeVariety.values();
        Random random = new Random();
        CoffeeVariety randomVariety = coffeeVarieties[random.nextInt(coffeeVarieties.length)];

        return randomVariety;
    }

    private static Collection<CoffeeBean> generateCoffeeBeans(CoffeeVariety coffeeVariety) {
        List<CoffeeBean> coffeeBeans = new ArrayList<>();
        for (int i = 0; i < new Random().nextInt(1, 5); i++) {
            CoffeeBean coffeeBean = new CoffeeBean(new Random().nextInt(50, 200), coffeeVariety);
            coffeeBeans.add(coffeeBean);
        }

        return coffeeBeans;
    }

    private void delay() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
