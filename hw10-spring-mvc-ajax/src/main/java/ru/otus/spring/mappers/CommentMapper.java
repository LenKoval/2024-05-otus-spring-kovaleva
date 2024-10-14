package ru.otus.spring.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.spring.dtos.CommentDto;
import ru.otus.spring.dtos.CommentUpdateDto;
import ru.otus.spring.models.Comment;

@RequiredArgsConstructor
@Component
public class CommentMapper {

    public CommentDto toDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getBook().getId());
    }

    public CommentUpdateDto toViewDto(Comment comment) {
        return new CommentUpdateDto(comment.getId(), comment.getText());
    }
}