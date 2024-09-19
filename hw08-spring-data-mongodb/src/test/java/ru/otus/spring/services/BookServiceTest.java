package ru.otus.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.dtos.AuthorDto;
import ru.otus.spring.dtos.BookDto;
import ru.otus.spring.dtos.CommentDto;
import ru.otus.spring.dtos.GenreDto;
import ru.otus.spring.event.BookDeletionEventListener;
import ru.otus.spring.mappers.AuthorMapper;
import ru.otus.spring.mappers.BookMapper;
import ru.otus.spring.mappers.CommentMapper;
import ru.otus.spring.mappers.GenreMapper;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Сервис для работы с книгами ")
@DataMongoTest
@Import({BookMapper.class, AuthorMapper.class, GenreMapper.class, CommentMapper.class,
        BookServiceImpl.class, GenreServiceImpl.class,
        AuthorServiceImpl.class, CommentServiceImpl.class, BookDeletionEventListener.class})
@Transactional(propagation = Propagation.NEVER)
public class BookServiceTest {

    @Autowired
    private BookServiceImpl bookService;

    @Autowired
    private CommentServiceImpl commentService;

    private List<AuthorDto> dbAuthors;

    private List<GenreDto> dbGenres;

    private List<CommentDto> dbComments;

    private List<BookDto> dbBooks;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
        dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
        dbComments = getDbComments(dbBooks);
    }

    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldReturnCorrectBookById(BookDto expectedBook) {
        var returnedBook = bookService.findById(expectedBook.getId());

        assertThat(returnedBook).isPresent()
                .get()
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
        var expectedBook = new BookDto("BookTitle_10500", dbAuthors.get(0),
                List.of(dbGenres.get(0), dbGenres.get(1)));

        var returnedBookDto = bookService.create("BookTitle_10500", dbAuthors.get(0).getId(),
                Set.of(dbGenres.get(0).getId(), dbGenres.get(1).getId()));

        assertThat(returnedBookDto).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);
    }

    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var expectedBook = new BookDto("1", "BookTitle_10500", dbAuthors.get(0),
                List.of(dbGenres.get(0), dbGenres.get(1)));


        var returnedBook = bookService.update("1", "BookTitle_10500", dbAuthors.get(0).getId(),
                Set.of(dbGenres.get(0).getId(), dbGenres.get(1).getId()));

        assertThat(returnedBook).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("должен удалять книгу по id")
    @Test
    void shouldDeleteBook() {

        BookDto expectedBook = bookService.findAll().get(0);
        var expectedComment = commentService.create("NewText", expectedBook.getId());

        List<CommentDto> expectedComments = commentService.findById(expectedBook.getId());
        assertFalse(expectedComments.isEmpty());

        bookService.deleteById(expectedBook.getId());

        var actualBook = bookService.findById(expectedBook.getId());
        List<CommentDto> actualComments = commentService.findById(expectedBook.getId());
        assertThat(actualBook).isEmpty();
        assertTrue(actualComments.isEmpty());
    }

    private static List<AuthorDto> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new AuthorDto(String.format("%s", id), "Author_" + id))
                .toList();
    }

    private static List<GenreDto> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new GenreDto(String.format("%s", id), "Genre_" + id))
                .toList();
    }

    private static List<CommentDto> getDbComments(List<BookDto> dbBooks) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new CommentDto(String.format("%s", id), "Comment_" + id, dbBooks.get(id - 1)))
                .toList();
    }

    private static List<BookDto> getDbBooks(List<AuthorDto> dbAuthors, List<GenreDto> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new BookDto(String.format("%s", id),
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
