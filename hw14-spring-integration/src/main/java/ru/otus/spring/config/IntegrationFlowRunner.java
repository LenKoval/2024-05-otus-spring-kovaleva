package ru.otus.spring.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.otus.spring.services.GrinderService;

@Component
@RequiredArgsConstructor
public class IntegrationFlowRunner implements CommandLineRunner {

    private final GrinderService grinderService;

    @Override
    public void run(String... args) {

        grinderService.startGrindCoffeeBean();
    }
}
