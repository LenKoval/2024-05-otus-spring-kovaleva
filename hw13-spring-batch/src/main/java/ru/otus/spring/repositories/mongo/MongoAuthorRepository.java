package ru.otus.spring.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.spring.models.mongo.MongoAuthor;

public interface MongoAuthorRepository extends MongoRepository<MongoAuthor, String> {
}
