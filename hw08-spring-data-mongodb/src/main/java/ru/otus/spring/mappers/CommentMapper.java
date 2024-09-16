package ru.otus.spring.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.spring.dtos.CommentDto;
import ru.otus.spring.models.Comment;

@RequiredArgsConstructor
@Component
public class CommentMapper {

    private final BookMapper bookMapper;

    public CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(), comment.getText(), bookMapper.toDto(comment.getBook()));
    }
}
