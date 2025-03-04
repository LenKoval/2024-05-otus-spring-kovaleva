package ru.otus.spring.services;

import ru.otus.spring.dtos.BookCreateDto;
import ru.otus.spring.dtos.BookDto;
import ru.otus.spring.dtos.BookUpdateDto;

import java.util.List;

public interface BookService {
    BookDto findById(long id);

    List<BookDto> findAll();

    BookDto create(BookCreateDto bookCreateDto);

    BookDto update(BookUpdateDto bookUpdateDto);

    void deleteById(long id);
}
