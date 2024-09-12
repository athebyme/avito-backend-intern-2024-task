package io.codefresh.gradleexample.dao.entities.organizations;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    UUID id;
    String name;
    String description;
    @Enumerated(EnumType.STRING)
    OrganizationTypes type;
    Timestamp created_at;
    Timestamp updated_at;
}
