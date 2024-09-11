package ru.otus.spring.converters;

import org.springframework.stereotype.Component;
import ru.otus.spring.dtos.AuthorDto;

@Component
public class AuthorConverter {
    public String authorToString(AuthorDto authorDto) {
        return "Id: %s, FullName: %s".formatted(authorDto.getId(), authorDto.getFullName());
    }
}
