package io.codefresh.gradleexample.dao.entities;

import java.sql.Timestamp;
import java.util.UUID;

public class OrganizationEntity {
    UUID id;
    String name;
    String description;
    OrganizationTypesEnum organization_type;
    Timestamp created_at;
    Timestamp updated_at;
}
