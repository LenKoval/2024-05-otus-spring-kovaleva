package ru.otus.spring.repositories;

import ru.otus.spring.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    Comment save(Comment comment);

    void deleteById(long id);

    Optional<Comment> findById(long id);

    List<Comment> findByBookId(long bookId);
}
