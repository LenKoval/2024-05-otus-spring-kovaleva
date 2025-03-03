package ru.otus.spring.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.otus.spring.services.CafeService;

@Component
@RequiredArgsConstructor
public class IntegrationFlowRunner implements CommandLineRunner {

    private final CafeService cafeService;

    @Override
    public void run(String... args) {
        cafeService.startPreparedCoffeeCups();
    }
}
