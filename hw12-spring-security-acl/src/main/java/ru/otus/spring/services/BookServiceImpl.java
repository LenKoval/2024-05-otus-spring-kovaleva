package ru.otus.spring.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.dtos.BookCreateDto;
import ru.otus.spring.dtos.BookDto;
import ru.otus.spring.dtos.BookUpdateDto;
import ru.otus.spring.exceptions.EntityNotFoundException;
import ru.otus.spring.mappers.BookMapper;
import ru.otus.spring.models.Book;
import ru.otus.spring.repositories.AuthorRepository;
import ru.otus.spring.repositories.BookRepository;
import ru.otus.spring.repositories.CommentRepository;
import ru.otus.spring.repositories.GenreRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final GenreRepository genreRepository;

    private final AuthorRepository authorRepository;

    private final CommentRepository commentRepository;

    private final BookMapper bookMapper;

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    @Transactional()
    @Override
    public BookDto findById(long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(id)));
        return bookMapper.toDto(book);
    }

    @PostFilter("hasPermission(filterObject, 'READ')")
    @Transactional(readOnly = true)
    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Override
    public BookDto create(BookCreateDto bookCreateDto) {
        Book book = new Book(0, bookCreateDto.getTitle(),
                authorRepository.findById(bookCreateDto.getAuthor())
                        .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found"
                                .formatted(bookCreateDto.getAuthor()))),
                genreRepository.findAllById(bookCreateDto.getGenres()));
        bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    @PreAuthorize("hasPermission(#bookUpdateDto, 'WRITE')")
    @Transactional
    @Override
    public BookDto update(@Param("bookUpdateDto") BookUpdateDto bookUpdateDto) {
        Book book = bookRepository.findById(bookUpdateDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found"
                        .formatted(bookUpdateDto.getId())));

        book.setTitle(bookUpdateDto.getTitle());
        book.setAuthor(authorRepository.findById(bookUpdateDto.getAuthor())
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found"
                        .formatted(bookUpdateDto.getId()))));
        book.setGenres(genreRepository.findAllById(bookUpdateDto.getGenres()));
        bookRepository.save(book);

        return bookMapper.toDto(book);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Override
    public void deleteById(long id) {
        commentRepository.deleteByBookId(id);
        bookRepository.deleteById(id);
    }
}
