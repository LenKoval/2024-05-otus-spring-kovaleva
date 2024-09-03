package ru.otus.spring.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.dtos.BookDto;
import ru.otus.spring.exceptions.EntityNotFoundException;
import ru.otus.spring.mappers.BookMapper;
import ru.otus.spring.models.Book;
import ru.otus.spring.repositories.BookRepository;

import java.util.Optional;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Autowired
    private final GenreService genreService;

    @Autowired
    private final AuthorService authorService;

    private final BookMapper bookMapper;

    @Transactional(readOnly = true)
    @Override
    public Optional<BookDto> findById(long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toDto);
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
    public BookDto create(String title, long authorId, Set<Long> genresIds) {
        if (title.isEmpty()) {
            throw new IllegalArgumentException("Title must not be null");
        }

        Book book = new Book(0, title, authorService.findById(authorId), genreService.isValid(genresIds));
        bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    @Transactional
    @Override
    public BookDto update(long id, String title, long authorId, Set<Long> genresIds) {
        Book book = isValid(id);

        book.setTitle(title);
        book.setAuthor(authorService.findById(authorId));
        book.setGenres(genreService.isValid(genresIds));
        bookRepository.save(book);

        return bookMapper.toDto(book);
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public Book isValid(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(id)));
    }
}
