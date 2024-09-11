package ru.otus.spring.services;

import ru.otus.spring.dtos.CommentDto;

import java.util.Optional;

public interface CommentService {

    CommentDto create(String text, String bookId);

    CommentDto update(String id, String text);

    Optional<CommentDto> findById(String id);
}
