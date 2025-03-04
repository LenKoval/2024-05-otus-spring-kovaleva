package ru.otus.spring.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import ru.otus.spring.models.Book;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "books")
public interface BookRepository extends JpaRepository<Book, Long> {

    @RestResource(path = "by-id", rel = "find-by-id")
    @EntityGraph(value = "book-entity-graph")
    Optional<Book> findById(long id);

    @RestResource(path = "all", rel = "find-all")
    @EntityGraph(value = "book-author-genres-entity-graph")
    List<Book> findAll();
}
