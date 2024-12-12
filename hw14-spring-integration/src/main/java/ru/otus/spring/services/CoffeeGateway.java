package ru.otus.spring.services;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.spring.models.CoffeeBean;
import ru.otus.spring.models.GroundCoffee;

import java.util.Collection;

@MessagingGateway
public interface CoffeeGateway {

    @Gateway(requestChannel = "coffeeBeanChannel", replyChannel = "groundCoffeeChannel")
    Collection<GroundCoffee> process(Collection<CoffeeBean> coffeeBeans);
}
