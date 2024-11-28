package ru.otus.spring.repositories.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.otus.spring.models.jpa.JpaAuthor;

public interface JpaAuthorRepository extends PagingAndSortingRepository<JpaAuthor, Long> {
}
