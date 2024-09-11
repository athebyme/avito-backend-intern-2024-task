package io.codefresh.gradleexample.dao.entities.users;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
public class EmployeeEntity
{
    @Id
    UUID id;
    String username;
    String first_name;
    String last_name;
    Timestamp created_at;
    Timestamp updated_at;
}
