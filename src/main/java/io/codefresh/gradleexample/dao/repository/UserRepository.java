package io.codefresh.gradleexample.dao.repository;

import io.codefresh.gradleexample.dao.entities.users.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<EmployeeEntity, UUID> {
}
