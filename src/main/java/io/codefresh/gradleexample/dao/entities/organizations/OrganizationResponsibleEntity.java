package io.codefresh.gradleexample.dao.entities.organizations;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;


@Entity
public class OrganizationResponsibleEntity {
    @Id
    UUID id;
    @Id
    UUID organization_id;
    @Id
    UUID user_id;
}
