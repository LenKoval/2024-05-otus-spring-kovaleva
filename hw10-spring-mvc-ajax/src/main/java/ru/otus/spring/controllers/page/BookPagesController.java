package ru.otus.spring.controllers.page;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class BookPagesController {

    @GetMapping
    public String getPageBookList() {
        return "page-book-list";
    }

    @GetMapping("/books")
    public String getPageBook() {
        return "page-book";
    }

    @GetMapping("/books/{id}")
    public String getEditPage(@PathVariable long id, Model model) {
        model.addAttribute("bookId", id);
        return "page-book";
    }

}
