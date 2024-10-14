package ru.otus.spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.spring.controllers.rest.BookController;
import ru.otus.spring.dtos.*;
import ru.otus.spring.services.BookService;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    private List<AuthorDto> authorDtos;

    private List<GenreDto> genreDtos;

    private List<BookDto> booksDtos;

    @BeforeEach
    void setUp() {
        authorDtos = getDbAuthors();
        genreDtos = getDbGenres();
        booksDtos = getDbBooks(authorDtos, genreDtos);
    }

    @Test
    public void testCreateBook() throws Exception {
        var bookCreateDto = new BookCreateDto(
                "New_Book_Title",
                authorDtos.get(0).getId(),
                Set.of(genreDtos.get(0).getId(), genreDtos.get(1).getId()));

        var bookDto = new BookDto(4,
                "New_Book_Title",
                authorDtos.get(0),
                List.of(genreDtos.get(0), genreDtos.get(1)));

        when(bookService.create(any())).thenReturn(null);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bookCreateDto)))
                .andExpect(status().isCreated());
                //.andExpect(content().json(asJsonString(bookDto)));
    }

    @Test
    public void testUpdateBook() throws Exception {
        var bookDto = booksDtos.get(0);

        var bookUpdateDto = new BookUpdateDto(
                bookDto.getId(),
                "Update_Book_Title",
                authorDtos.get(0).getId(),
                Set.of(genreDtos.get(0).getId(), genreDtos.get(1).getId()));

        when(bookService.update(any())).thenReturn(null);

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bookUpdateDto)))
                .andExpect(status().isOk());
                //.andExpect(content().json(asJsonString(bookDto)));
    }

    @Test
    public void testGetAllBooks() throws Exception {
        when(bookService.findAll()).thenReturn(booksDtos);

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(booksDtos)));
    }

    @Test
    public void testGetBookById() throws Exception {
        var bookDto = booksDtos.get(0);

        when(bookService.findById(1L)).thenReturn(bookDto);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(bookDto)));
    }

    @Test
    public void testDeleteBook() throws Exception {
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }

    private String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
