package io.codefresh.gradleexample.dao.entities.organizations;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
public class Organization {
    @Id
    UUID id;
    String name;
    String description;
    OrganizationTypes type;
    Timestamp created_at;
    Timestamp updated_at;
}
