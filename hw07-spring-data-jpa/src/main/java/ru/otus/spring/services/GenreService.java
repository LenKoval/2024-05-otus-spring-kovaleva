package ru.otus.spring.services;

import ru.otus.spring.dtos.GenreDto;
import ru.otus.spring.models.Genre;

import java.util.List;
import java.util.Set;

public interface GenreService {
    List<GenreDto> findAll();

    List<Genre> isValid(Set<Long> genresIds);
}
