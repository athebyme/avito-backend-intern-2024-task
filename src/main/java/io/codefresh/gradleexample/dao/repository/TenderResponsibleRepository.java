package io.codefresh.gradleexample.dao.repository;

import io.codefresh.gradleexample.dao.entities.organizations.OrganizationResponsible;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TenderResponsibleRepository extends JpaRepository<OrganizationResponsible, UUID> {
    boolean existsByOrganizationIdAndEmployeeId(UUID organizationId, UUID employeeId);
}
