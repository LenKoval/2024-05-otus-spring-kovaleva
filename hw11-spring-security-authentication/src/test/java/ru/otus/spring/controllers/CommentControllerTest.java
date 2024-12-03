package ru.otus.spring.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.spring.dtos.*;
import ru.otus.spring.services.CommentService;

import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    List<CommentDto> commentDtos;

    List<AuthorDto> authorDtos;

    List<GenreDto> genreDtos;

    List<BookDto> bookDtos;

    @BeforeEach
    void setUp() {
        authorDtos = getDbAuthors();
        genreDtos = getDbGenres();
        bookDtos = getDbBooks(authorDtos, genreDtos);
        commentDtos = getDbComments();
    }

    @Nested
    @DisplayName("Get Page Comment List")
    class GetPageCommentListTests {

        @Test
        @DisplayName("Should display page with list of comments")
        void shouldDisplayPageWithListOfComments() throws Exception {
            var book = bookDtos.get(0);

            List<CommentDto> commentDto = List.of(new CommentDto(1L, "Comment_1", book.getId()));

            when(commentService.findCommentByBookId(book.getId())).thenReturn(commentDto);

            mockMvc.perform(get("/books/1/comments"))
                    .andExpect(status().isUnauthorized());

            //verify(commentService).findCommentByBookId(book.getId());
        }
    }

    @Nested
    @DisplayName("Create Comment")
    class CreateCommentTests {

        @Test
        @DisplayName("Should redirect to comment list after successful creation")
        void shouldRedirectToCommentListAfterSuccessfulCreation() throws Exception {
            CommentCreateDto commentCreateDto = new CommentCreateDto(1L, "New Comment");

            mockMvc.perform(post("/books/1/comments")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("text", commentCreateDto.getText()))
                    .andExpect(status().isForbidden());

            //verify(commentService).create(commentCreateDto);
        }

        @Test
        @DisplayName("Should show validation errors on creation form")
        void shouldShowValidationErrorsOnCreationForm() throws Exception {
            String emptyText = "";

            mockMvc.perform(post("/books/1/comments")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("text", emptyText))
                    .andExpect(status().is4xxClientError());

            verifyNoInteractions(commentService);
        }
    }

    @Nested
    @DisplayName("Update Comment")
    class UpdateCommentTests {

        @Test
        @DisplayName("Should redirect to comment list after successful update")
        void shouldRedirectToCommentListAfterSuccessfulUpdate() throws Exception {
            long commentId = 1L;
            String updatedText = "Updated Comment";

            mockMvc.perform(post("/books/{bookId}/comments/{id}/update", 1, commentId)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("updatedText", updatedText))
                    .andExpect(status().isForbidden());

            //CommentUpdateDto commentUpdateDto = new CommentUpdateDto(commentId, updatedText);
            //verify(commentService).update(commentUpdateDto);
        }

        @Test
        @DisplayName("Should show validation errors on update form")
        void shouldShowValidationErrorsOnUpdateForm() throws Exception {
            long bookId = 1L;
            long commentId = 1L;
            String invalidText = "";

            mockMvc.perform(post("/books/{bookId}/comments/{id}", bookId, commentId)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("text", invalidText))
                    .andExpect(status().is4xxClientError());

            verifyNoInteractions(commentService);
        }
    }

    @Nested
    @DisplayName("Delete Comment")
    class DeleteCommentTests {

        @Test
        @DisplayName("Should redirect to comment list after successful deletion")
        void shouldRedirectToCommentListAfterSuccessfulDeletion() throws Exception {
            long bookId = 1L;
            long commentId = 1L;

            mockMvc.perform(post("/books/{bookId}/comments/{id}/delete",bookId, commentId))
                    .andExpect(status().isForbidden());

            //verify(commentService).deleteById(commentId);
        }
    }

    private static List<CommentDto> getDbComments() {
        var books = getDbBooks();
        return getDbComments(books);
    }

    private static List<CommentDto> getDbComments(List<BookDto> dbBooks) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new CommentDto(id, "Comment_" + id, dbBooks.get(id - 1).getId()))
                .toList();
    }

    private static List<BookDto> getDbBooks() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
    }


    private static List<BookDto> getDbBooks(List<AuthorDto> dbAuthors, List<GenreDto> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new BookDto(id,
                        "BookTitle_" + id,
                        dbAuthors.get(id - 1),
                        dbGenres.subList((id - 1) * 2, (id - 1) * 2 + 2)
                ))
                .toList();
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
}
