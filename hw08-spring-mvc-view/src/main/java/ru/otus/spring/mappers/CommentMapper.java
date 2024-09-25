package ru.otus.spring.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.spring.dtos.CommentDto;
import ru.otus.spring.dtos.CommentViewDto;
import ru.otus.spring.models.Comment;

@RequiredArgsConstructor
@Component
public class CommentMapper {

    public CommentDto toDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getBook().getId());
    }

    public CommentViewDto toViewDto(Comment comment) {
        return new CommentViewDto(comment.getId(), comment.getText());
    }
}
