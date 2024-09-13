package io.codefresh.gradleexample.dao.repository;

import io.codefresh.gradleexample.dao.entities.tenders.TenderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenderHistoryRepository extends JpaRepository<TenderHistory, UUID> {
    Optional<TenderHistory> findByTenderIdAndVersion(UUID tenderId, int version);
}
