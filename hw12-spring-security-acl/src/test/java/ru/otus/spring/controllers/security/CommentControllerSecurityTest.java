package ru.otus.spring.controllers.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.spring.controllers.CommentController;
import ru.otus.spring.dtos.*;
import ru.otus.spring.repositories.UserRepository;
import ru.otus.spring.security.SecurityConfiguration;
import ru.otus.spring.services.CommentService;

import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@Import(SecurityConfiguration.class)
public class CommentControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

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

    @WithMockUser
    @Test
    void shouldProvideAccessMethodGetForAuthUsers() throws Exception {
        var book = bookDtos.get(0);
        List<CommentDto> commentDto = List.of(new CommentDto(1L, "Comment_1", book.getId()));

        when(commentService.findCommentByBookId(book.getId())).thenReturn(commentDto);

        mockMvc.perform(get("/books/1/comments"))
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    @Test
    void shouldProvideAccessMethodPostForAdmin() throws Exception {
        CommentCreateDto commentCreateDto = new CommentCreateDto(1L, "New Comment");
        long commentId = 1L;
        String updatedText = "Updated Comment";

        mockMvc.perform(post("/books/1/comments")
                        .param("text", commentCreateDto.getText())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/1/comments"));

        mockMvc.perform(post("/books/{bookId}/comments/{id}/update", 1, commentId)
                        .param("updatedText", updatedText)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/1/comments"));

        mockMvc.perform(post("/books/1/comments/1/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/1/comments"));
    }

    @WithMockUser(username = "user", authorities = {"ROLE_USER"})
    @Test
    void shouldDenyAccessMethodPostForUser() throws Exception {
        long commentId = 1L;
        String updatedText = "Updated Comment";

        mockMvc.perform(post("/books/{bookId}/comments/{id}/update", 1, commentId)
                        .param("updatedText", updatedText)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(post("/books/1/comments/1/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verifyNoMoreInteractions(commentService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/books/1/comments", "/books/1/comments/1/update", "/books/1/comments/1/delete"})
    void testUnauthorizedActions(String urlTemplate) throws Exception {

        mockMvc.perform(get(urlTemplate)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        mockMvc.perform(post(urlTemplate)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        verifyNoMoreInteractions(commentService);
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
