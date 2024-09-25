package ru.otus.spring.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CommentViewDto {

    private long id;

    private String text;
}
