package io.codefresh.gradleexample.dao.repository;

import io.codefresh.gradleexample.dao.entities.users.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<EmployeeEntity, UUID> {
}
