package io.codefresh.gradleexample.dao.repository;

import io.codefresh.gradleexample.dao.entities.tenders.Tender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TenderRepository extends JpaRepository<Tender, UUID> {
    List<Tender> findByName(String name);
}
