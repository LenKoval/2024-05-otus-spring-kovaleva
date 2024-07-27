package ru.otus.spring.repositories;

import org.springframework.jdbc.core.RowMapper;
import ru.otus.spring.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcAuthorRepository implements AuthorRepository {

    @Override
    public List<Author> findAll() {
        return new ArrayList<>();
    }

    @Override
    public Optional<Author> findById(long id) {
        return Optional.empty();
    }

    private static class AuthorRowMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet rs, int rowNum) throws SQLException {
            return null;
        }
    }
}
