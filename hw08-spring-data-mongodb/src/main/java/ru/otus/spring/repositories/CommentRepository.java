package ru.otus.spring.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ru.otus.spring.models.Comment;

import java.util.Optional;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

    @Query(value = "{ 'book._id': ?0 }")
    Optional<Comment> findByBookId(String bookId);
}
