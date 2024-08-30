package ru.otus.spring.converters;

import org.springframework.stereotype.Component;
import ru.otus.spring.dtos.CommentDto;

@Component
public class CommentConverter {

    public String commentToString(CommentDto commentDto) {
        return "Book: %s, Comment: %s, Id: %s"
                .formatted(commentDto.getBookTitle(), commentDto.getText(), commentDto.getId());
    }
}
