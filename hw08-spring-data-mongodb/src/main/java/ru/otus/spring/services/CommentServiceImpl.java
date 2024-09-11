package ru.otus.spring.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.dtos.CommentDto;
import ru.otus.spring.exceptions.EntityNotFoundException;
import ru.otus.spring.mappers.CommentMapper;
import ru.otus.spring.models.Comment;
import ru.otus.spring.repositories.CommentRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final BookService bookService;

    @Transactional
    @Override
    public CommentDto create(String text, String bookId) {
        Comment comment = new Comment(text, bookService.isValid(bookId));

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public CommentDto update(String id, String text) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id %s not found".formatted(id)));

        comment.setText(text);

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public Optional<CommentDto> findById(String id) {
        return commentRepository.findByBookId(id)
                .map(commentMapper::toDto);
    }
}
