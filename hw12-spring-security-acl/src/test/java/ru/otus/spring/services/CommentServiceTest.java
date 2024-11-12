package ru.otus.spring.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.dtos.BookCreateDto;
import ru.otus.spring.dtos.CommentCreateDto;
import ru.otus.spring.dtos.CommentUpdateDto;
import ru.otus.spring.exceptions.EntityNotFoundException;
import ru.otus.spring.mappers.AuthorMapper;
import ru.otus.spring.mappers.BookMapper;
import ru.otus.spring.mappers.CommentMapper;
import ru.otus.spring.mappers.GenreMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Сервис для работы с комментариями")
@DataJpaTest
@Import({CommentServiceImpl.class, CommentMapper.class, BookMapper.class, AuthorMapper.class, GenreMapper.class})
@Transactional(propagation = Propagation.NEVER)
public class CommentServiceTest {

    @Autowired
    private CommentServiceImpl commentService;

    @Test
    @DisplayName("должен загружать комментарий по id")
    void shouldReturnCorrectCommentById() {
        var actualComment = commentService.findById(1L);
        assertThat(actualComment).isNotNull();
        assertThat(actualComment.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("должен обновлять комментарий")
    void shouldUpdateComment() {
        var comment = commentService.findById(1L);

        var commentUpdateDto = new CommentUpdateDto(comment.getId(), "Updated Text");

        var expectedComment = commentService.update(commentUpdateDto);

        var returnedBook = commentService.findById(1L);

        assertThat(expectedComment).isNotNull();
        assertThat(expectedComment.getId()).isEqualTo(returnedBook.getId());
    }

    @Test
    @DisplayName("должен сохранять комментарий")
    void shouldSaveComment() {
        var commentCreateDto = new CommentCreateDto(1L, "New Text");
        var expectedComment = commentService.create(commentCreateDto);
        var returnedComment = commentService.findById(expectedComment.getId());

        assertThat(returnedComment).isNotNull();
        assertThat(expectedComment.getId()).isEqualTo(returnedComment.getId());
    }

    @DisplayName("должен удалять комментарий по id")
    @Test
    void shouldDeleteComment() {
        var commentCreateDto = new CommentCreateDto(1L, "New text");
        var commentDto = commentService.create(commentCreateDto);
        commentService.deleteById(commentDto.getId());
        assertThrows(EntityNotFoundException.class, () -> commentService.findById(commentDto.getId()));
    }
}
