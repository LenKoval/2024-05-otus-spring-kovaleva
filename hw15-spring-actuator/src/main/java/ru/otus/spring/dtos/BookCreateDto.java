package ru.otus.spring.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class BookCreateDto {

    @NotBlank
    private String title;

    @NotNull
    private Long author;

    @NotEmpty
    private Set<@NotNull Long> genres;
}
