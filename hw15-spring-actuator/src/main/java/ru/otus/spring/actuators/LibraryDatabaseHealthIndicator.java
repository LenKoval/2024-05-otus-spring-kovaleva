package ru.otus.spring.actuators;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import ru.otus.spring.repositories.CommentRepository;

@RequiredArgsConstructor
@Component
@Slf4j
public class LibraryDatabaseHealthIndicator implements HealthIndicator {

    private final CommentRepository commentRepository;

    @Override
    public Health health() {
        int storageComments;
        try {
            storageComments = (int) commentRepository.count();
        } catch (RuntimeException e) {
            log.error("Error while accessing comment repository", e);
            return getStatusDown();
        }
        if (storageComments == 0) {
            return getStatusDown();
        } else {
            return Health.up().withDetail("message", storageComments + " comments in storage!").build();
        }
    }

    private static Health getStatusDown() {
        return Health.down()
                .withDetail("message", "No comments")
                .build();
    }
}
