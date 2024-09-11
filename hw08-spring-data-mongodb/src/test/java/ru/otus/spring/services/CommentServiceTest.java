package ru.otus.spring.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.mappers.AuthorMapper;
import ru.otus.spring.mappers.BookMapper;
import ru.otus.spring.mappers.CommentMapper;
import ru.otus.spring.mappers.GenreMapper;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с комментариями")
@DataMongoTest
@Import({CommentServiceImpl.class, CommentMapper.class, BookMapper.class, AuthorMapper.class, GenreMapper.class,
        BookServiceImpl.class, GenreServiceImpl.class, AuthorServiceImpl.class})
@Transactional(propagation = Propagation.NEVER)
public class CommentServiceTest {

    @Autowired
    private CommentServiceImpl commentService;

    @Test
    @DisplayName("должен загружать комментарий по id")
    void shouldReturnCorrectCommentById() {
        var actualComment = commentService.findById("1");
        assertThat(actualComment).isPresent();
        assertThat(actualComment.get().getId()).isEqualTo("1");
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("должен обновлять комментарий")
    void shouldUpdateComment() {
        var comment = commentService.findById("1");

        var expectedComment = commentService.update(comment.get().getId(), "New text");

        var returnedBook = commentService.findById("1");
        assertThat(expectedComment).isNotNull();

        assertThat(expectedComment.getId()).isEqualTo(returnedBook.get().getId());
    }

    @Test
    @DisplayName("должен сохранять комментарий")
    void shouldSaveComment() {
        var expectedComment = commentService.create("Text", "1");
        var returnedComment = commentService.findById("1");

        assertThat(returnedComment).isPresent();
        assertThat(expectedComment.getId()).isEqualTo(returnedComment.get().getId());
    }
}
