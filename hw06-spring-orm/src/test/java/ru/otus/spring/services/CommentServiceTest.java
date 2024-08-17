package ru.otus.spring.services;

import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.spring.repositories.JpaCommentRepository;

@DisplayName("Сервис для работы с комментариями")
@DataJpaTest
@Import({JpaCommentRepository.class, CommentServiceImpl.class})
public class CommentServiceTest {
}
