package ru.otus.spring.processor;

import jakarta.annotation.Nonnull;
import org.springframework.batch.item.ItemProcessor;
import ru.otus.spring.models.jpa.JpaAuthor;
import ru.otus.spring.models.mongo.MongoAuthor;

public class AuthorProcessor implements ItemProcessor<JpaAuthor, MongoAuthor> {

    @Override
    public MongoAuthor process(@Nonnull JpaAuthor jpaAuthor) {
        return new MongoAuthor(String.valueOf(jpaAuthor.getId()), jpaAuthor.getFullName());
    }
}
