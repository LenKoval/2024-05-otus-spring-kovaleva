package ru.otus.spring.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class ValidationErrorResponseDto {

    private int statusCode;

    private String message;

    private List<String> messages;
}
