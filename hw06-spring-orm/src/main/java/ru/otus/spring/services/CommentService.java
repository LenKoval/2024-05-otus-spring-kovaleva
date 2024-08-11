package ru.otus.spring.services;

import ru.otus.spring.dtos.CommentDto;

import java.util.Optional;

public interface CommentService {

    CommentDto insert(String text, long bookId);

    CommentDto update(long id, String text, long bookId);

    Optional<CommentDto> findById(long id);
}
