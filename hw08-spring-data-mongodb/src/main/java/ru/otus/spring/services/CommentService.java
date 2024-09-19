package ru.otus.spring.services;

import ru.otus.spring.dtos.CommentDto;

import java.util.List;

public interface CommentService {

    CommentDto create(String text, String bookId);

    CommentDto update(String id, String text);

    List<CommentDto> findById(String id);
}
