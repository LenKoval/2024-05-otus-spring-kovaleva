package ru.otus.spring.controllers.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.spring.controllers.BookController;
import ru.otus.spring.dtos.*;
import ru.otus.spring.repositories.UserRepository;
import ru.otus.spring.security.SecurityConfiguration;
import ru.otus.spring.services.AuthorService;
import ru.otus.spring.services.BookService;
import ru.otus.spring.services.GenreService;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@Import(SecurityConfiguration.class)
public class BookControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    private List<AuthorDto> authorDtos;

    private List<GenreDto> genreDtos;

    private List<BookDto> booksDtos;

    @BeforeEach
    void setUp() {
        authorDtos = getDbAuthors();
        genreDtos = getDbGenres();
        booksDtos = getDbBooks(authorDtos, genreDtos);
    }

    @WithMockUser
    @Test
    void shouldProvideAccessMethodGetForAuthUser() throws Exception {
        List<BookDto> books = booksDtos;
        when(bookService.findAll()).thenReturn(books);

        BookDto book = new BookDto(1L, "Existing Book", authorDtos.get(0),
                List.of(genreDtos.get(0), genreDtos.get(1)));

        when(bookService.findById(anyLong())).thenReturn(book);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/books/{id}", book.getId())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("book"))
                .andExpect(view().name("page-book"));
    }

    @WithMockUser
    @Test
    void shouldProvideAccessMethodPostForAuthUser() throws Exception {
        BookUpdateDto bookUpdateDto = new BookUpdateDto(booksDtos.get(0).getId(), "Update Title",
                authorDtos.get(1).getId(), Set.of(genreDtos.get(1).getId(), genreDtos.get(2).getId()));

        mockMvc.perform(post("/books/{id}", bookUpdateDto.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", bookUpdateDto.getTitle())
                        .param("author", "2")
                        .param("genres", "2,3")
                        .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/books/" + bookUpdateDto.getId()));

        BookCreateDto bookCreateDto = new BookCreateDto("New Title", authorDtos.get(0).getId(),
                Set.of(genreDtos.get(0).getId(), genreDtos.get(1).getId()));

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", bookCreateDto.getTitle())
                        .param("author", "1")
                        .param("genres", "1,2")
                        .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/"));

        var book = booksDtos.get(0);

        mockMvc.perform(post("/books/{id}/delete", book.getId())
                        .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    void testUnauthorizedActions() throws Exception {

        mockMvc.perform(get("/books"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/books/1")
                .with(csrf()))
                .andExpect(status().isUnauthorized());
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
}
