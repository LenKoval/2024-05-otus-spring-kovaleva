package ru.otus.spring.processor;

import jakarta.annotation.Nonnull;
import org.springframework.batch.item.ItemProcessor;
import ru.otus.spring.models.jpa.JpaGenre;
import ru.otus.spring.models.mongo.MongoGenre;

public class GenreProcessor implements ItemProcessor<JpaGenre, MongoGenre> {

    @Override
    public MongoGenre process(@Nonnull JpaGenre jpaGenre) {
        return new MongoGenre(String.valueOf(jpaGenre.getId()), jpaGenre.getName());
    }
}
