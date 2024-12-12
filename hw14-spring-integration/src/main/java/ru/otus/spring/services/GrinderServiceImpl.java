package ru.otus.spring.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.spring.models.CoffeeBean;
import ru.otus.spring.models.GroundCoffee;

import java.util.Collection;
import java.util.concurrent.ForkJoinPool;

@Service
@RequiredArgsConstructor
@Slf4j
public class GrinderServiceImpl implements GrinderService {

    private final CoffeeBeanService coffeeBeanService;

    private final CoffeeGateway coffeeGateway;

    @Override
    public void startGrindCoffeeBean() {
        ForkJoinPool.commonPool().execute(() -> {
            Collection<CoffeeBean> coffeeBeans = coffeeBeanService.generateCoffeeBeanList();
            log.info("New coffeeBeans: {}",
                    coffeeBeans.stream().map(CoffeeBean::getVariety)
                            .toList());

            Collection<GroundCoffee> groundCoffee = coffeeGateway.process(coffeeBeans);
            log.info("Ready groundCoffee: {}", groundCoffee.stream()
                    .map(GroundCoffee::getCoffeeVariety)
                    .toList());
        });
    }
}
