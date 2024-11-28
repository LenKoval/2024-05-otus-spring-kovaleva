package ru.otus.spring.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.spring.models.mongo.MongoGenre;

import java.util.List;
import java.util.Set;

public interface MongoGenreRepository extends MongoRepository<MongoGenre, Long> {

    List<MongoGenre> findAllByIdIn(Set<String> ids);
}
