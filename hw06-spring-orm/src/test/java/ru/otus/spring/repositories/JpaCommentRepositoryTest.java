package ru.otus.spring.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.spring.models.Book;
import ru.otus.spring.models.Comment;

import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с комментариями ")
@DataJpaTest
@Import({JpaCommentRepository.class, JpaBookRepository.class})
public class JpaCommentRepositoryTest {

    @Autowired
    JpaCommentRepository commentRepository;

    @Autowired
    JpaBookRepository bookRepository;

    @DisplayName("должен загружать комментарий по id")
    @ParameterizedTest
    @MethodSource("getDbComment")
    void shouldReturnCorrectCommentById(Comment expectedComment) {
        Optional<Comment> actualComment = commentRepository.findById(expectedComment.getId());
        assertThat(actualComment).isPresent()
                .get()
                .isEqualTo(expectedComment);
    }

    @Test
    @DisplayName("должен обновлять комментарий")
    void shouldUpdateComment() {
        var expectedComment = new Comment(1L, "TEXT_TEXT", bookRepository.findById(1L).orElseThrow());
        assertThat(commentRepository.findById(1L))
                .isPresent()
                .get()
                .isNotEqualTo(expectedComment);

        var returnedComment = commentRepository.save(expectedComment);
        assertThat(returnedComment).isEqualTo(expectedComment);
    }

    @Test
    @DisplayName("должен сохранять комментарий")
    void shouldSaveComment() {
        var expectedComment = new Comment(0L, "TEXT_TEXT", bookRepository.findById(1L).orElseThrow());
        assertThat(commentRepository.findById(expectedComment.getId())).isNotPresent();
        var returnedComment = commentRepository.save(expectedComment);
        assertThat(returnedComment).isNotNull().matches(comment -> comment.getId() == 5L);
        assertThat(commentRepository.findById(returnedComment.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedComment);
    }

    @Test
    @DisplayName("должен удалять комментарий")
    void shouldDeleteComment() {
        long idToDelete = 1;
        Optional<Comment> byId = commentRepository.findById(idToDelete);
        commentRepository.deleteById(idToDelete);
        assertThat(byId).isPresent();
        assertThat(commentRepository.findById(idToDelete)).isEmpty();
    }

    @Test
    @DisplayName("должен находить комментарии по id книги")
    void shouldFindCommentsByBook() {
        Book book = bookRepository.findById(1).orElseThrow();
        List<Comment> allCommentsByBookId = commentRepository.findByBookId(book.getId());
        assertThat(allCommentsByBookId).isNotEmpty()
                .containsAll(List.of(commentRepository.findById(1L).get()));
    }

    private static List<Comment> getDbComment() {
        return LongStream.range(1, 4).boxed()
                .map(id -> new Comment(id, "Comment_" + id, new Book(id, "", null, null)))
                .toList();
    }
}
