package ru.otus.spring.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.spring.models.mongo.MongoComment;

import java.util.List;

public interface MongoCommentRepository extends MongoRepository<MongoComment, String> {

    List<MongoComment> findAllByBookId(String bookId);
}
