package ru.otus.spring.services;

import ru.otus.spring.dtos.BookDto;
import ru.otus.spring.models.Book;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookService {
    Optional<BookDto> findById(String id);

    List<BookDto> findAll();

    BookDto create(String title, String authorId, Set<String> genresIds);

    BookDto update(String id, String title, String authorId, Set<String> genresIds);

    void deleteById(String id);

    Book isValid(String id);
}
