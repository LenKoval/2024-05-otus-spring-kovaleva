package ru.otus.spring.services;

import ru.otus.spring.dtos.AuthorDto;

import java.util.List;

public interface AuthorService {
    List<AuthorDto> findAll();

    AuthorDto findById(String id);
}
