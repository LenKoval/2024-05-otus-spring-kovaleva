package ru.otus.spring.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ru.otus.spring.models.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

    @Query(value = "{ 'book._id': ?0 }")
    List<Comment> findByBookId(String bookId);

    void deleteByBookId(String bookId);
}
