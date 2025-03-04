package ru.otus.spring.mappers;

import org.springframework.stereotype.Component;
import ru.otus.spring.dtos.AuthorDto;
import ru.otus.spring.models.Author;

@Component
public class AuthorMapper {

    public AuthorDto toDto(Author author) {
        return new AuthorDto(author.getId(), author.getFullName());
    }
}
