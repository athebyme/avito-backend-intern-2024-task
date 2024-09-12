package io.codefresh.gradleexample.dao.entities.organizations;

import javax.persistence.*;
import java.util.UUID;


@Entity
public class OrganizationResponsible {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;
    @Column(name = "organization_id")
    UUID organizationId;
    @Column(name = "user_id")
    UUID employeeId;
}
