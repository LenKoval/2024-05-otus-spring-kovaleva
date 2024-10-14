package ru.otus.spring.mappers;

import org.springframework.stereotype.Component;
import ru.otus.spring.dtos.GenreDto;
import ru.otus.spring.models.Genre;

@Component
public class GenreMapper {

    public GenreDto toDto(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }
}
