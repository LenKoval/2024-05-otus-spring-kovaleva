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
import ru.otus.spring.dtos.AuthorDto;
import ru.otus.spring.dtos.BookDto;
import ru.otus.spring.dtos.GenreDto;
import ru.otus.spring.mappers.AuthorMapper;
import ru.otus.spring.mappers.BookMapper;
import ru.otus.spring.mappers.GenreMapper;
import ru.otus.spring.repositories.JpaAuthorRepository;
import ru.otus.spring.repositories.JpaBookRepository;
import ru.otus.spring.repositories.JpaGenreRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("Сервис для работы с книгами ")
@DataJpaTest
@Import({JpaBookRepository.class, JpaGenreRepository.class, JpaAuthorRepository.class
        , BookMapper.class, AuthorMapper.class, GenreMapper.class, BookServiceImpl.class})
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
    void shouldReturnCorrectBookById(BookDto expectedBookDto) {
        var returnedBookDto = bookService.findById(expectedBookDto.getId());

        assertThat(returnedBookDto).isPresent()
                .get()
                .isEqualTo(expectedBookDto);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var returnedBooksDto = bookService.findAll();

        assertFalse(returnedBooksDto.isEmpty());
        assertEquals(3, returnedBooksDto.size());
        assertThat(returnedBooksDto).containsExactlyInAnyOrderElementsOf(dbBooks);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var expectedBookDto = new BookDto(1L, "BookTitle_10500", dbAuthors.get(0),
                List.of(dbGenres.get(0), dbGenres.get(2)));

        var returnedBookDto = bookService.create(expectedBookDto.getTitle(),
                expectedBookDto.getAuthorDto().getId(),
                expectedBookDto.getGenres().stream().map(GenreDto::getId).collect(Collectors.toSet()));

        assertThat(returnedBookDto).isEqualTo(expectedBookDto);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var expectedBookDto = new BookDto(1L, "BookTitle_10500", dbAuthors.get(2),
                List.of(dbGenres.get(4), dbGenres.get(3)));


        var returnedBookDto = bookService.update(expectedBookDto.getId(),
                expectedBookDto.getTitle(),
                expectedBookDto.getAuthorDto().getId(),
                expectedBookDto.getGenres().stream().map(GenreDto::getId).collect(Collectors.toSet()));

        assertThat(returnedBookDto).isEqualTo(expectedBookDto);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("должен удалять книгу по id")
    @Test
    void shouldDeleteBook() {
        assertThat(bookService.findById(1L)).isNotEmpty();

        assertThatCode(() -> bookService.deleteById(1L)).doesNotThrowAnyException();

        assertThat(bookService.findById(1L)).isEmpty();
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
