package ru.otus.spring.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.otus.spring.dtos.BookDto;
import ru.otus.spring.dtos.BookCreateDto;
import ru.otus.spring.dtos.BookUpdateDto;
import ru.otus.spring.services.BookService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/books")
    public BookDto createBook(@RequestBody @Validated BookCreateDto bookDto) {
        return bookService.create(bookDto);
    }

    @PutMapping("/api/books/{id}")
    public BookDto updateBook(@RequestBody @Validated BookUpdateDto bookDto) {
        return bookService.update(bookDto);
    }

    @GetMapping("/api/books")
    public List<BookDto> getAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/api/books/{id}")
    public BookDto getBookById(@PathVariable long id) {
        return bookService.findById(id);
    }

    @DeleteMapping("/api/books/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable long id) {
        bookService.deleteById(id);
    }
}
