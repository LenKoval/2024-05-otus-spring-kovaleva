package ru.otus.spring.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class BookDto {

    private String id;

    private String title;

    private AuthorDto authorDto;

    private List<GenreDto> genres;

    public BookDto(String title, AuthorDto authorDto, List<GenreDto> genres) {
        this.title = title;
        this.authorDto = authorDto;
        this.genres = genres;
    }
}
