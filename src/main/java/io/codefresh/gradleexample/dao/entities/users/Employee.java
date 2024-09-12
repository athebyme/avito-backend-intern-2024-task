package io.codefresh.gradleexample.dao.entities.users;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "employee")
public class Employee
{
    @Id
    UUID id;
    String username;
    String first_name;
    String last_name;
    Timestamp created_at;
    Timestamp updated_at;
}
