package ru.otus.spring.services;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.spring.models.CoffeeBean;
import ru.otus.spring.models.CoffeeCup;

import java.util.Collection;

@MessagingGateway
public interface CoffeeGateway {

    @Gateway(requestChannel = "coffeeBeanChannel", replyChannel = "coffeeCupChannel")
    Collection<CoffeeCup> process(Collection<CoffeeBean> coffeeBeans);
}
