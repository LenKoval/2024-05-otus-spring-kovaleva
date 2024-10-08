package ru.otus.spring.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.otus.spring.models.Book;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {
}
