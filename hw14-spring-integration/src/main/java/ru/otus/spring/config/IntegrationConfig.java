package ru.otus.spring.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.MessageChannelSpec;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.PollerSpec;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.scheduling.PollerMetadata;
import ru.otus.spring.models.CoffeeBean;
import ru.otus.spring.models.GroundCoffee;
import ru.otus.spring.services.PreparedCoffeeService;

@Configuration
@Slf4j
public class IntegrationConfig {

    @Bean
    public MessageChannelSpec<?, ?> coffeeBeanChannel() {
        return MessageChannels.queue(10);
    }

    @Bean
    public MessageChannelSpec<?, ?> coffeeCupChannel() {
        return MessageChannels.publishSubscribe();
    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerSpec poller() {
        return Pollers.fixedRate(100).maxMessagesPerPoll(2);
    }

    @Bean
    public IntegrationFlow coffeeCupFlow(PreparedCoffeeService coffeeService) {
        return IntegrationFlow.from(coffeeBeanChannel())
                .split()
                .<CoffeeBean, GroundCoffee>transform(bean -> new GroundCoffee(bean.getCount(), bean.getVariety()))
                .handle(coffeeService, "preparedCoffee")
                .log(LoggingHandler.Level.INFO, "coffeeCup-flow.preparedCoffee",
                        m -> "Coffee cup %s was prepared".formatted(m.getHeaders().getId()))
                .aggregate(a -> a.sendPartialResultOnExpiry(true).groupTimeout(1))
                .channel(coffeeCupChannel())
                .get();
    }
}
