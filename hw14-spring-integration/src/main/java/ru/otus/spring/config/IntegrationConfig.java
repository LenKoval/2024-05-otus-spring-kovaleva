package ru.otus.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.MessageChannelSpec;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.PollerSpec;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.Message;
import ru.otus.spring.models.CoffeeCup;
import ru.otus.spring.models.GroundCoffee;
import ru.otus.spring.models.CoffeeBean;
import ru.otus.spring.models.CoffeeBeverage;

import java.util.Random;
import java.util.random.RandomGenerator;

@Configuration
@EnableIntegration
public class IntegrationConfig {

    private final RandomGenerator randomGenerator = RandomGenerator.getDefault();

    @Bean
    public MessageChannelSpec<?, ?> coffeeBeanChannel() {
        return MessageChannels.queue(10);
    }

    @Bean
    public MessageChannelSpec<?, ?> groundCoffeeChannel() {
        return MessageChannels.publishSubscribe();
    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerSpec poller() {
        return Pollers.fixedRate(100).maxMessagesPerPoll(2);
    }

    @Bean
    public IntegrationFlow insectFlow() {
        return IntegrationFlow.from(coffeeBeanChannel())
                .split()
                .<CoffeeBean, GroundCoffee>transform(coffeeBean -> new GroundCoffee(
                        coffeeBean.getCount(),
                        coffeeBean.getVariety())
                )
                .<GroundCoffee, CoffeeCup>transform(groundCoffee -> new CoffeeCup(
                        CoffeeBeverage.values()[new Random().nextInt(CoffeeBeverage.values().length)])
                )
                .<CoffeeCup>log(LoggingHandler.Level.INFO, "coffeeCup", Message::getPayload)
                .aggregate()
                .channel(groundCoffeeChannel())
                .get();
    }
}
