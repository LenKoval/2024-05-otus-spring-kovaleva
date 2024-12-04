package ru.otus.spring.processor;

import jakarta.annotation.Nonnull;
import org.springframework.batch.item.ItemProcessor;
import ru.otus.spring.models.jpa.JpaComment;
import ru.otus.spring.models.mongo.MongoComment;

public class CommentProcessor implements ItemProcessor<JpaComment, MongoComment> {

    @Override
    public MongoComment process(@Nonnull JpaComment jpaComment) {
        BookProcessor bookProcessor = new BookProcessor();

        return new MongoComment(String.valueOf(jpaComment.getId()), jpaComment.getText(),
                bookProcessor.process(jpaComment.getBook()));
    }
}
