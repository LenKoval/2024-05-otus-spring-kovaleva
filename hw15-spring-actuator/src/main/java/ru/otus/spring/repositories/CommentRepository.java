package ru.otus.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import ru.otus.spring.models.Comment;

import java.util.List;

@RepositoryRestResource(path = "comments")
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @RestResource(path = "by-bookId", rel = "comments-by-bookId")
    List<Comment> findCommentsByBookId(long bookId);

    @RestResource(path = "delete-by-bookId", rel = "delete-comments-by-bookId")
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Comment c where c.book.id = :bookId")
    void deleteByBookId(@Param("bookId") Long bookId);
}
