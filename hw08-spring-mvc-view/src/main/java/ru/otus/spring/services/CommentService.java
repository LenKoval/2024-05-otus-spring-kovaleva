package ru.otus.spring.services;

import ru.otus.spring.dtos.CommentDto;
import ru.otus.spring.dtos.CommentViewDto;
import ru.otus.spring.dtos.CommentViewNotIdDto;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    CommentDto create(String text, long bookId);

    CommentDto update(long id, String text);

    List<CommentViewDto> findCommentByBookId(long bookId);

    Optional<CommentDto> findById(long id);

    void deleteById(long id);
}
