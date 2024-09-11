package io.codefresh.gradleexample.repository;

import io.codefresh.gradleexample.dao.entities.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public abstract interface UserRepository extends JpaRepository<UUID, EmployeeEntity> {
}
