package ru.otus.spring.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GenreDto {

    private long id;

    private String name;
}
