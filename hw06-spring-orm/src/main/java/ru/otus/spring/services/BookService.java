package ru.otus.spring.services;

import ru.otus.spring.dtos.BookDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookService {
    Optional<BookDto> findById(long id);

    List<BookDto> findAll();

    BookDto create(String title, long authorId, Set<Long> genresIds);

    BookDto update(long id, String title, long authorId, Set<Long> genresIds);

    void deleteById(long id);
}
