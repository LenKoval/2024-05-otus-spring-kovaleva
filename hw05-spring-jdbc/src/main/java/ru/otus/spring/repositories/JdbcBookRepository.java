package ru.otus.spring.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.spring.exceptions.EntityNotFoundException;
import ru.otus.spring.models.Author;
import ru.otus.spring.models.Book;
import ru.otus.spring.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final GenreRepository genreRepository;

    @Override
    public Optional<Book> findById(long id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        List<Book> books = jdbcTemplate.query("""                        
                        SELECT b.id        as book_id
                             , b.title     AS book_title
                             , a.id        AS author_id
                             , a.full_name AS author_name
                             , g.id        AS genre_id
                             , g.name      AS genre_name
                          FROM      books         b
                         INNER JOIN authors       a ON a.id       = b.author_id
                         INNER JOIN books_genres bg ON bg.book_id = b.id
                         INNER JOIN genres        g ON g.id       = bg.genre_id
                         WHERE b.id = :id
                        """,
                params,
                new BookResultSetExtractor()
        );
        return books != null ? books.stream().findFirst() : Optional.empty();
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        var books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        jdbcTemplate.update("DELETE FROM books WHERE id =:id", params);
    }

    private List<Book> getAllBooksWithoutGenres() {
        return jdbcTemplate.query("""                        
                        SELECT b.id        as book_id
                             , b.title     AS book_title
                             , a.id        AS author_id
                             , a.full_name AS author_name
                          FROM      books   b
                         INNER JOIN authors a ON a.id       = b.author_id
                        """,
                new BookRowMapper()
        );
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        return jdbcTemplate.query("SELECT book_id, genre_id FROM books_genres", new BookGenreRowMapper());
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        Map<Long, Book> mapBooks = booksWithoutGenres.stream()
                .collect(Collectors.toMap(Book::getId, book -> book, (a, b) -> b));
        Map<Long, Genre> mapGenre = genres.stream()
                .collect(Collectors.toMap(Genre::getId, genre -> genre, (a, b) -> b));

        relations.forEach(relation -> mapBooks.get(relation.bookId())
                .getGenres().add(mapGenre.get(relation.genreId())));
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", book.getTitle());
        params.addValue("author_id", book.getAuthor().getId());
        jdbcTemplate.update("INSERT INTO books(title, author_id) VALUES (:title, :author_id)",
                params,
                keyHolder,
                new String[]{"id"}
        );

        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", book.getTitle());
        params.addValue("author_id", book.getAuthor().getId());
        params.addValue("id", book.getId());
        var rowUpdated = jdbcTemplate.update("""
                        UPDATE books 
                        SET title = :title
                        , author_id = :author_id 
                        where id = :id
                        """,
                params
        );

        if (rowUpdated == 0) {
            throw new EntityNotFoundException("No rows was updated.");
        }

        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        List<Genre> genres = book.getGenres();

        SqlParameterSource[] params = genres.stream()
                .map(genre -> new MapSqlParameterSource()
                        .addValue("book_id", book.getId())
                        .addValue("genre_id", genre.getId())
                )
                .toList()
                .toArray(SqlParameterSource[]::new);
        jdbcTemplate.batchUpdate("INSERT INTO books_genres(book_id, genre_id) VALUES (:book_id, :genre_id)", params);
    }

    private void removeGenresRelationsFor(Book book) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("book_id", book.getId());
        jdbcTemplate.update("DELETE FROM books_genres WHERE book_id = :book_id", params);
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            long bookId = rs.getLong("book_id");
            String bookTitle = rs.getString("book_title");
            Author author = new Author(rs.getLong("author_id"),rs.getString("author_name"));
            return new Book(bookId, bookTitle, author, new ArrayList<>());
        }
    }

    private static class BookGenreRowMapper implements RowMapper<BookGenreRelation> {

        @Override
        public BookGenreRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
            long bookId = rs.getLong("book_id");
            long genreId = rs.getLong("genre_id");
            return new BookGenreRelation(bookId, genreId);
        }
    }

    // Использовать для findById
    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<List<Book>> {

        @Override
        public List<Book> extractData(ResultSet rs) throws SQLException, DataAccessException {
            Map<Long, Book> map = new HashMap<>();
            while (rs.next()) {
                long bookId = rs.getLong("book_id");
                Genre genre = new Genre(rs.getLong("genre_id"),rs.getString("genre_name"));
                Book book = map.get(bookId);
                if (book == null) {
                    Author author = new Author(rs.getLong("author_id"),rs.getString("author_name"));
                    book = new Book(bookId, rs.getString("book_title"), author, new ArrayList<>(List.of(genre)));
                    map.put(book.getId(), book);
                } else {
                    book.getGenres().add(genre);
                }
            }
            return new ArrayList<>(map.values());
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}
