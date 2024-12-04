package ru.otus.spring.processor;

import jakarta.annotation.Nonnull;
import org.springframework.batch.item.ItemProcessor;
import ru.otus.spring.models.jpa.JpaBook;
import ru.otus.spring.models.mongo.MongoAuthor;
import ru.otus.spring.models.mongo.MongoBook;
import ru.otus.spring.models.mongo.MongoGenre;

import java.util.List;
import java.util.stream.Collectors;

public class BookProcessor implements ItemProcessor<JpaBook, MongoBook> {

    @Override
    public MongoBook process(@Nonnull JpaBook jpaBook) {
        AuthorProcessor authorProcessor = new AuthorProcessor();
        MongoAuthor mongoAuthor = authorProcessor.process(jpaBook.getAuthor());
        GenreProcessor genreProcessor = new GenreProcessor();
        List<MongoGenre> mongoGenres = jpaBook.getGenres().stream()
                .map(genreProcessor::process)
                .collect(Collectors.toList());

        return new MongoBook(String.valueOf(jpaBook.getId()), jpaBook.getTitle(), mongoAuthor, mongoGenres);
    }
}
