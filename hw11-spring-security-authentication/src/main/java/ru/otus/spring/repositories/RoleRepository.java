package ru.otus.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.spring.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
