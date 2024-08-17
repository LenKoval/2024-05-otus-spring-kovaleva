package ru.otus.spring.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.spring.models.Book;
import ru.otus.spring.models.Comment;

import java.util.Optional;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            entityManager.persist(book);
            return book;
        }
        return entityManager.merge(book);
    }

    @Override
    public void deleteById(long id) {
        entityManager.remove(entityManager.find(Book.class, id));
    }

    @Override
    public Optional<Book> findById(long id) {
        return entityManager.createQuery("select b from Book b where b.id = :id", Book.class)
                .setParameter("id", id)
                .setHint("jakarta.persistence.fetchgraph"
                        , entityManager.getEntityGraph("book-author-genres-entity-graph"))
                .getResultList().stream().findAny();
    }

    @Override
    public List<Book> findAll() {
        return entityManager.createQuery("from Book", Book.class)
                .setHint("jakarta.persistence.fetchgraph", entityManager.getEntityGraph("book-entity-graph"))
                .getResultList();
    }
}
