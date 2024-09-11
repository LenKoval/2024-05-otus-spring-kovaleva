package ru.otus.spring.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.otus.spring.models.Author;

@Repository
public interface AuthorRepository extends MongoRepository<Author, String> {
}
