package io.codefresh.gradleexample.dao.repository.bids;

import io.codefresh.gradleexample.dao.entities.bids.Decision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DecisionRepository extends JpaRepository<Decision, UUID> {
    List<Decision> findByBidId(UUID bidId);
}