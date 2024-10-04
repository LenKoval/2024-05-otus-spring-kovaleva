package ru.otus.spring.services;

import ru.otus.spring.dtos.CommentCreateDto;
import ru.otus.spring.dtos.CommentDto;
import ru.otus.spring.dtos.CommentUpdateDto;

import java.util.List;

public interface CommentService {

    CommentDto create(CommentCreateDto commentCreateDto);

    CommentDto update(CommentUpdateDto commentUpdateDto);

    List<CommentDto> findCommentByBookId(long bookId);

    CommentDto findById(long id);

    void deleteById(long id);
}
