package ru.otus.spring.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.mappers.CommentMapper;
import ru.otus.spring.repositories.JpaBookRepository;
import ru.otus.spring.repositories.JpaCommentRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с комментариями")
@DataJpaTest
@Import({JpaCommentRepository.class, CommentServiceImpl.class
        , JpaBookRepository.class, JpaCommentRepository.class, CommentMapper.class})
@Transactional(propagation = Propagation.NEVER)
public class CommentServiceTest {

    @Autowired
    private CommentServiceImpl commentService;

    @Test
    @DisplayName("должен загружать комментарий по id")
    void shouldReturnCorrectCommentById() {
        var actualComment = commentService.findById(1L);
        assertThat(actualComment).isPresent();
        assertThat(actualComment.get().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("должен обновлять комментарий")
    void shouldUpdateComment() {
        var comment = commentService.findById(1L);

        var expectedComment = commentService.update(comment.get().getId(), "New text");

        var returnedBook = commentService.findById(1L);
        assertThat(expectedComment).isNotNull();

        assertThat(expectedComment.getId()).isEqualTo(returnedBook.get().getId());
    }

    @Test
    @DisplayName("должен сохранять комментарий")
    void shouldSaveComment() {
        var expectedComment = commentService.create("New text", 1L);
        var returnedComment = commentService.findById(expectedComment.getId());

        assertThat(returnedComment).isNotNull();
        assertThat(expectedComment.getId()).isEqualTo(returnedComment.get().getId());
    }
}
