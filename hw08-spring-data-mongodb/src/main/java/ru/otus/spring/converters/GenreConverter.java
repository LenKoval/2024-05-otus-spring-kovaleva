package ru.otus.spring.converters;

import org.springframework.stereotype.Component;
import ru.otus.spring.dtos.GenreDto;

@Component
public class GenreConverter {
    public String genreToString(GenreDto genreDto) {
        return "Id: %s, Name: %s".formatted(genreDto.getId(), genreDto.getName());
    }
}
