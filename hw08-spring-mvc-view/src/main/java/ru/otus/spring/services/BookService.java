package ru.otus.spring.services;

import ru.otus.spring.dtos.BookDto;
import ru.otus.spring.dtos.BookViewDto;
import ru.otus.spring.models.Book;

import java.util.List;
import java.util.Set;

public interface BookService {
    BookViewDto findById(long id);

    List<BookDto> findAll();

    BookDto create(String title, long authorId, Set<Long> genresIds);

    BookDto update(long id, String title, long authorId, Set<Long> genresIds);

    void deleteById(long id);

    Book isValid(Long id);
}
