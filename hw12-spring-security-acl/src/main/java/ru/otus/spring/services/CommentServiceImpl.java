package ru.otus.spring.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.dtos.CommentCreateDto;
import ru.otus.spring.dtos.CommentDto;
import ru.otus.spring.dtos.CommentUpdateDto;
import ru.otus.spring.exceptions.EntityNotFoundException;
import ru.otus.spring.mappers.CommentMapper;
import ru.otus.spring.models.Book;
import ru.otus.spring.models.Comment;
import ru.otus.spring.repositories.BookRepository;
import ru.otus.spring.repositories.CommentRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final BookRepository bookRepository;

    @Transactional
    @Override
    public CommentDto create(CommentCreateDto commentCreateDto) {
        Book book = bookRepository.findById(commentCreateDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Book with Id %d not found"
                .formatted(commentCreateDto.getBookId())));
        Comment comment = new Comment(0, commentCreateDto.getText(), book);

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    @Override
    public CommentDto update(CommentUpdateDto commentUpdateDto) {
        Comment comment = commentRepository.findById(commentUpdateDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Comment with id %d not found"
                        .formatted(commentUpdateDto.getId())));

        comment.setText(commentUpdateDto.getText());

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional()
    @Override
    public List<CommentDto> findCommentByBookId(long bookId) {
        return commentRepository.findCommentsByBookId(bookId)
                .stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public CommentDto findById(long id) {
        return commentMapper.toDto(commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id %d not found"
                .formatted(id))));
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }
}
