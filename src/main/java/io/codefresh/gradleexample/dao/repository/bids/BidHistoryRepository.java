package io.codefresh.gradleexample.dao.repository.bids;

import io.codefresh.gradleexample.dao.entities.bids.BidHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BidHistoryRepository extends JpaRepository<BidHistory, UUID> {
    Optional<BidHistory> findByBidIdAndVersion(UUID bidId, int version);
}
