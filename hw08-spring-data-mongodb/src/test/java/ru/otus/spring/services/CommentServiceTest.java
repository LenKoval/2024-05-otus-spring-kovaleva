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
import ru.otus.spring.mappers.AuthorMapper;
import ru.otus.spring.mappers.BookMapper;
import ru.otus.spring.mappers.CommentMapper;
import ru.otus.spring.mappers.GenreMapper;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Сервис для работы с комментариями")
@DataMongoTest
@Import({CommentServiceImpl.class, CommentMapper.class, BookMapper.class, AuthorMapper.class, GenreMapper.class,
        BookServiceImpl.class, GenreServiceImpl.class, AuthorServiceImpl.class})
@Transactional(propagation = Propagation.NEVER)
public class CommentServiceTest {

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

    @ParameterizedTest
    @DisplayName("должен загружать комментарий по id")
    @MethodSource("getDbBooks")
    void shouldReturnCorrectCommentById(BookDto bookDto) {
        List<CommentDto> actualComment = commentService.findById(bookDto.getId());
        assertThat(actualComment).isNotEmpty();
        assertThat(actualComment.stream().allMatch(commentDto -> commentDto.getBookDto().equals(bookDto)));
        assertThat(actualComment)
                .hasSizeGreaterThan(0)
                .extracting(CommentDto::getId)
                .contains(bookDto.getId());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("должен обновлять комментарий")
    void shouldUpdateComment() {
        String text = "text";
        BookDto expectedBook = dbBooks.get(0);

        CommentDto actual = commentService.update(expectedBook.getId(), text);

        assertEquals(text, actual.getText());
        assertThat(actual.getBookDto())
                .usingRecursiveComparison()
                .isEqualTo(expectedBook);
    }

    @Test
    @DisplayName("должен сохранять комментарий")
    void shouldSaveComment() {
        String text = "New text";
        BookDto expectedBook = dbBooks.get(0);

        CommentDto actual = commentService.create(text, expectedBook.getId());

        assertEquals(text, actual.getText());
        assertThat(actual.getBookDto())
                .usingRecursiveComparison()
                .isEqualTo(expectedBook);
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
