package ru.otus.spring.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class BookViewNotIdDto {

    @NotBlank
    private String title;

    @NotNull
    private Long authorId;

    @NotEmpty
    private Set<@NotNull Long> genresIds;
}
