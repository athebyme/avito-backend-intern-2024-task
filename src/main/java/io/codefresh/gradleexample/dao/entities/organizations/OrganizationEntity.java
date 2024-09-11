package io.codefresh.gradleexample.dao.entities.organizations;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
public class OrganizationEntity {
    @Id
    UUID id;
    String name;
    String description;
    OrganizationTypesEnum organization_type;
    Timestamp created_at;
    Timestamp updated_at;
}
