package ru.otus.spring.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.otus.spring.dtos.BookDto;
import ru.otus.spring.dtos.BookCreateDto;
import ru.otus.spring.dtos.BookUpdateDto;
import ru.otus.spring.services.AuthorService;
import ru.otus.spring.services.BookService;
import ru.otus.spring.services.GenreService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    @GetMapping()
    public String getPageBookList(Model model) {
        List<BookDto> books = bookService.findAll();
        model.addAttribute("books", books);

        return "page-book-list";
    }

    @GetMapping("/books")
    public String createPage(Model model) {
        model.addAttribute("bookId", null);
        model.addAttribute("book", new BookCreateDto());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());

        return "page-book";
    }

    @GetMapping("/books/{id}")
    public String getEditPage(@PathVariable long id, Model model) {
        model.addAttribute("bookId", id);
        model.addAttribute("book", bookService.findById(id));
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());

        return "page-book";
    }

    @PostMapping("/books/{id}")
    public String update(@PathVariable long id,
                         @ModelAttribute("book") @Valid BookUpdateDto bookDto,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("genres", genreService.findAll());
            return "page-book";
        }

        bookService.update(bookDto);
        return "redirect:/books/" + id;
    }

    @PostMapping("/books")
    public String create(@ModelAttribute("book") @Valid BookCreateDto bookDto,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("genres", genreService.findAll());
            return "page-book";
        }

        bookService.create(bookDto);

        return "redirect:/";
    }

    @PostMapping("/books/{id}/delete")
    public String delete(@PathVariable long id) {
        bookService.deleteById(id);

        return "redirect:/";
    }
}
