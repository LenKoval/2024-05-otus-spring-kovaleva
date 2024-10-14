package ru.otus.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.dtos.*;
import ru.otus.spring.exceptions.EntityNotFoundException;
import ru.otus.spring.mappers.AuthorMapper;
import ru.otus.spring.mappers.BookMapper;
import ru.otus.spring.mappers.GenreMapper;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Сервис для работы с книгами ")
@DataJpaTest
@Import({BookMapper.class, AuthorMapper.class, GenreMapper.class, BookServiceImpl.class})
@Transactional(propagation = Propagation.NEVER)
public class BookServiceTest {

    @Autowired
    private BookServiceImpl bookService;

    private List<AuthorDto> dbAuthors;

    private List<GenreDto> dbGenres;

    private List<BookDto> dbBooks;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
        dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
    }

    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldReturnCorrectBookById(BookDto expectedBook) {
        var returnedBook = bookService.findById(expectedBook.getId());

        assertThat(returnedBook)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedBook);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var returnedBooks = bookService.findAll();

        assertFalse(returnedBooks.isEmpty());
        assertEquals(3, returnedBooks.size());
        assertThat(returnedBooks)
                .usingRecursiveComparison()
                .isEqualTo(dbBooks);
    }

    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var expectedBook = new BookDto(4L, "BookTitle_10500", dbAuthors.get(0),
                List.of(dbGenres.get(0), dbGenres.get(1)));

        var bookCreateDto = new BookCreateDto("BookTitle_10500", dbAuthors.get(0).getId(),
                Set.of(dbGenres.get(0).getId(), dbGenres.get(1).getId()));

        var returnedBookDto = bookService.create(bookCreateDto);

        assertThat(returnedBookDto).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);
    }

    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var expectedBook = new BookDto(1L, "BookTitle_10500", dbAuthors.get(0),
                List.of(dbGenres.get(0), dbGenres.get(1)));

        var bookUpdateDto = new BookUpdateDto(1L, "BookTitle_10500", dbAuthors.get(0).getId(),
                Set.of(dbGenres.get(0).getId(), dbGenres.get(1).getId()));

        var returnedBook = bookService.update(bookUpdateDto);

        assertThat(returnedBook).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);
    }

    @DisplayName("должен удалять книгу по id")
    @Test
    void shouldDeleteBook() {
        var bookCreateDto = new BookCreateDto("NewBook", 1L, Set.of(1L, 2L));
        var book = bookService.create(bookCreateDto);
        bookService.deleteById(book.getId());
        assertThrows(EntityNotFoundException.class, () -> bookService.findById(book.getId()));
    }

    private static List<AuthorDto> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new AuthorDto(id, "Author_" + id))
                .toList();
    }

    private static List<GenreDto> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new GenreDto(id, "Genre_" + id))
                .toList();
    }

    private static List<BookDto> getDbBooks(List<AuthorDto> dbAuthors, List<GenreDto> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new BookDto(id,
                        "BookTitle_" + id,
                        dbAuthors.get((id - 1)),
                        dbGenres.subList(((id - 1) * 2), ((id - 1) * 2 + 2))
                ))
                .toList();
    }

    private static List<BookDto> getDbBooks() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
    }
}
