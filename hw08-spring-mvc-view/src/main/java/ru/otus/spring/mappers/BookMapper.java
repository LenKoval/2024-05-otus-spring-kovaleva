package ru.otus.spring.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.spring.dtos.BookDto;
import ru.otus.spring.dtos.BookUpdateDto;
import ru.otus.spring.models.Book;
import ru.otus.spring.models.Genre;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookMapper {

    private final AuthorMapper authorMapper;

    private final GenreMapper genreMapper;

    public BookDto toDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                authorMapper.toDto(book.getAuthor()),
                book.getGenres().stream()
                        .map(genreMapper::toDto)
                        .toList());
    }

    public BookUpdateDto toViewDto(Book book) {
        return new BookUpdateDto(book.getId(),
                book.getTitle(),
                book.getAuthor().getId(),
                book.getGenres().stream()
                        .map(Genre::getId)
                        .collect(Collectors.toSet()));
    }
}
