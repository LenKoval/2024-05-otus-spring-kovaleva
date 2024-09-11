package ru.otus.spring.services;

import ru.otus.spring.dtos.AuthorDto;
import ru.otus.spring.models.Author;

import java.util.List;

public interface AuthorService {
    List<AuthorDto> findAll();

    Author findById(String id);
}
