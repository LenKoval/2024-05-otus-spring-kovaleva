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
import ru.otus.spring.services.AuthorService;
import ru.otus.spring.services.BookService;
import ru.otus.spring.services.CommentService;
import ru.otus.spring.services.GenreService;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    @MockBean
    private CommentService commentService;

    private List<AuthorDto> authorDtos;

    private List<GenreDto> genreDtos;

    private List<BookDto> booksDtos;

    @BeforeEach
    void setUp() {
        authorDtos = getDbAuthors();
        genreDtos = getDbGenres();
        booksDtos = getDbBooks(authorDtos, genreDtos);
    }

    @Nested
    @DisplayName("Get Page Book List")
    class GetPageBookListTests {

        @Test
        @DisplayName("Should display page with list of books")
        void shouldDisplayPageWithListOfBooks() throws Exception {
            List<BookDto> books = booksDtos;
            when(bookService.findAll()).thenReturn(books);

            mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("page-book-list"));

            verify(bookService).findAll();
        }
    }

    @Nested
    @DisplayName("Create Page")
    class CreatePageTests {

        @Test
        @DisplayName("Should display create form with authors and genres")
        void shouldDisplayCreateFormWithAuthorsAndGenres() throws Exception {
            List<AuthorDto> authors = authorDtos;
            List<GenreDto> genres = genreDtos;

            when(authorService.findAll()).thenReturn(authors);
            when(genreService.findAll()).thenReturn(genres);

            mockMvc.perform(get("/books"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("authors"))
                    .andExpect(model().attributeExists("genres"))
                    .andExpect(view().name("page-book"));

            verify(authorService).findAll();
            verify(genreService).findAll();
        }
    }

    @Nested
    @DisplayName("Get Edit Page")
    class GetEditPageTests {

        @Test
        @DisplayName("Should display edit form with book details")
        void shouldDisplayEditFormWithBookDetails() throws Exception {
            List<GenreDto> genres = IntStream.range(0, 2)
                    .mapToObj(i -> genreDtos.get(new Random().nextInt(genreDtos.size())))
                    .toList();

            BookViewDto book = new BookViewDto(1L, "Existing Book", authorDtos.get(0).getId(),
                    genres.stream().map(GenreDto::getId).collect(Collectors.toSet()));

            when(bookService.findById(anyLong())).thenReturn(book);

            mockMvc.perform(get("/books/1"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("book"))
                    .andExpect(view().name("page-book"));

            verify(bookService).findById(1L);
        }
    }

    @Nested
    @DisplayName("Update Book Page")
    class UpdateBookPageTests {

        @Test
        @DisplayName("Should redirect to book detail page after successful update")
        void shouldRedirectToBookDetailPageAfterSuccessfulUpdate() throws Exception {
            long id = 1L;
            String title = "Updated Title";

            mockMvc.perform(post("/books/{id}", id)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("title", title)
                            .param("authorId", "1")
                            .param("genresIds", "1,2"))
                    .andExpect(status().isFound())
                    .andExpect(view().name("redirect:/books/" + id));

            verify(bookService).update(id, title, 1L, Set.of(1L, 2L));
        }

        @Test
        @DisplayName("Should show validation errors on update form")
        void shouldShowValidationErrorsOnUpdateForm() throws Exception {
            long id = 1L;

            mockMvc.perform(post("/books/{id}", id)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("title", ""))
                    .andExpect(status().isOk())
                    .andExpect(view().name("page-book"));

            verifyNoInteractions(bookService);
        }
    }

    @Nested
    @DisplayName("Create Book")
    class CreateBookPageTests {

        @Test
        @DisplayName("Should redirect to home page after successful create")
        void shouldRedirectToHomePageAfterSuccessfulCreate() throws Exception {
            mockMvc.perform(post("/books")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("title", "New Book")
                            .param("authorId", "1")
                            .param("genresIds", "1,2"))
                    .andExpect(status().isFound())
                    .andExpect(view().name("redirect:/"));

            verify(bookService).create("New Book", 1L, Set.of(1L, 2L));
        }

        @Test
        @DisplayName("Should show validation errors on create form")
        void shouldShowValidationErrorsOnCreateForm() throws Exception {
            mockMvc.perform(post("/books")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("title", ""))
                    .andExpect(status().isOk())
                    .andExpect(view().name("page-book"));

            verifyNoInteractions(bookService);
        }
    }

    @Nested
    @DisplayName("Delete Page")
    class DeletePageTests {

        @Test
        @DisplayName("Should redirect to home page after successful delete")
        void shouldRedirectToHomePageAfterSuccessfulDelete() throws Exception {
            var book = booksDtos.get(0);

            mockMvc.perform(post("/books/{id}/delete", book.getId()))
                    .andExpect(status().isFound())
                    .andExpect(view().name("redirect:/"));

            verify(commentService).findCommentByBookId(book.getId());
            verify(bookService).deleteById(book.getId());
        }
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
