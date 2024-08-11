package ru.otus.spring.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.spring.models.Comment;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaCommentRepository implements CommentRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == 0) {
            entityManager.persist(comment);
            return comment;
        }
        return entityManager.merge(comment);
    }

    @Override
    public void deleteById(long id) {
        entityManager.remove(entityManager.find(Comment.class, id));
    }

    @Override
    public Optional<Comment> findById(long id) {
        return entityManager.createQuery("select c from Comment c where c.id = :id", Comment.class)
                .setParameter("id", id)
                .setHint("jakarta.persistence.fetchgraph", entityManager.getEntityGraph("comment-entity-graph"))
                .getResultList().stream().findAny();
    }

    @Override
    public List<Comment> findByBookId(long bookId) {
        return entityManager.createQuery("select c from Comment c where c.book.id = :bookId", Comment.class)
                .setParameter("bookId", bookId)
                .getResultList();
    }
}
