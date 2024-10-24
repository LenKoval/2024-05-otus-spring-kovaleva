package ru.otus.spring.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.dtos.BookCreateDto;
import ru.otus.spring.dtos.BookDto;
import ru.otus.spring.dtos.BookUpdateDto;
import ru.otus.spring.exceptions.NotFoundException;
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

    @Transactional()
    @Override
    public BookDto findById(long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book with id %d not found".formatted(id)));
        return bookMapper.toDto(book);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public BookDto create(BookCreateDto bookCreateDto) {
        Book book = new Book(0, bookCreateDto.getTitle(),
                authorRepository.findById(bookCreateDto.getAuthor())
                        .orElseThrow(() -> new NotFoundException("Author with id %d not found"
                                .formatted(bookCreateDto.getAuthor()))),
                genreRepository.findAllById(bookCreateDto.getGenres()));
        bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    @Transactional
    @Override
    public BookDto update(BookUpdateDto bookUpdateDto) {
        Book book = bookRepository.findById(bookUpdateDto.getId())
                .orElseThrow(() -> new NotFoundException("Book with id %d not found"
                        .formatted(bookUpdateDto.getId())));

        book.setTitle(bookUpdateDto.getTitle());
        book.setAuthor(authorRepository.findById(bookUpdateDto.getAuthor())
                .orElseThrow(() -> new NotFoundException("Book with id %d not found"
                        .formatted(bookUpdateDto.getId()))));
        book.setGenres(genreRepository.findAllById(bookUpdateDto.getGenres()));
        bookRepository.save(book);

        return bookMapper.toDto(book);
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        commentRepository.deleteByBookId(id);
        bookRepository.deleteById(id);
    }
}
