package ru.otus.spring.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class BookDto {

    private long id;

    private String title;

    private AuthorDto authorDto;

    private List<GenreDto> genres;
}
