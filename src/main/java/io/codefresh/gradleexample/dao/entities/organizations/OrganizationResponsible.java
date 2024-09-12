package io.codefresh.gradleexample.dao.entities.organizations;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;


@Entity
public class OrganizationResponsible {
    @Id
    UUID id;
    UUID organization_id;
    UUID user_id;
}
