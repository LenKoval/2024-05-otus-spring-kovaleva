package ru.otus.spring.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.spring.dtos.AuthorDto;
import ru.otus.spring.services.AuthorService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("/api/authors")
    public List<AuthorDto> getAllAuthors() {
        return authorService.findAll();
    }
}
