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
import ru.otus.spring.exceptions.EntityNotFoundException;
import ru.otus.spring.services.AuthorService;
import ru.otus.spring.services.BookService;
import ru.otus.spring.services.CommentService;
import ru.otus.spring.services.GenreService;

import java.util.List;
import java.util.Random;
import java.util.Set;
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

            BookDto book = new BookDto(1L, "Existing Book", authorDtos.get(0),
                    List.of(genreDtos.get(0), genreDtos.get(1)));

            when(bookService.findById(anyLong())).thenReturn(book);

            mockMvc.perform(get("/books/1"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("book"))
                    .andExpect(view().name("page-book"));

            verify(bookService).findById(1L);
        }

        @Test
        @DisplayName("Should return 404 for invalid book ID")
        void shouldReturn404ForInvalidBookId() throws Exception {
            when(bookService.findById(anyLong())).thenThrow(new EntityNotFoundException("Book not found"));

            mockMvc.perform(get("/books/9999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 404 for invalid genres ID")
        void shouldReturn404ForInvalidGenresId() throws Exception {
            BookDto book = new BookDto(3L, "Not found Book", authorDtos.get(0), null);

            when(genreService.findAll()).thenThrow(new EntityNotFoundException("Author not found"));

            mockMvc.perform(get("/books/3"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Update Book Page")
    class UpdateBookPageTests {

        @Test
        @DisplayName("Should redirect to book detail page after successful update")
        void shouldRedirectToBookDetailPageAfterSuccessfulUpdate() throws Exception {
            BookUpdateDto bookUpdateDto = new BookUpdateDto(booksDtos.get(0).getId(), "Update Title",
                    authorDtos.get(1).getId(), Set.of(genreDtos.get(1).getId(), genreDtos.get(2).getId()));

            mockMvc.perform(post("/books/{id}", bookUpdateDto.getId())
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("title", bookUpdateDto.getTitle())
                            .param("author", "2")
                            .param("genres", "2,3"))
                    .andExpect(status().isFound())
                    .andExpect(view().name("redirect:/books/" + bookUpdateDto.getId()));

            //verify(bookService).update(bookUpdateDto);
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
            BookCreateDto bookCreateDto = new BookCreateDto("New Title", authorDtos.get(0).getId(),
                    Set.of(genreDtos.get(0).getId(), genreDtos.get(1).getId()));

            mockMvc.perform(post("/books")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("title", bookCreateDto.getTitle())
                            .param("author", "1")
                            .param("genres", "1,2"))
                    .andExpect(status().isFound())
                    .andExpect(view().name("redirect:/"));

            //verify(bookService).create(bookCreateDto);
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
