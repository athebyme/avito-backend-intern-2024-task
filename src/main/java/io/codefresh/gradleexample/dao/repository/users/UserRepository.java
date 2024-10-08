package io.codefresh.gradleexample.dao.repository.users;

import io.codefresh.gradleexample.dao.entities.users.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Employee, UUID> {
    Employee findUserByUsername(String username);
}
