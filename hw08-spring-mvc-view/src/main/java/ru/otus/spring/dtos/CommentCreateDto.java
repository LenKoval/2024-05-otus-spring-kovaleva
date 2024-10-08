package ru.otus.spring.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommentCreateDto {

    @NotNull
    private Long bookId;

    @NotBlank
    private String text;

    public CommentCreateDto(String text) {
        this.text = text;
    }
}
