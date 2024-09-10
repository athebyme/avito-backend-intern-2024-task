package io.codefresh.gradleexample.dao.entities;


import java.sql.Timestamp;
import java.util.UUID;

public class EmployeeEntity
{
    UUID id;
    String username;
    String first_name;
    String last_name;
    Timestamp created_at;
    Timestamp updated_at;
}
