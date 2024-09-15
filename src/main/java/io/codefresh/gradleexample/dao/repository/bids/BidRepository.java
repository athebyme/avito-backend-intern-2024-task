package io.codefresh.gradleexample.dao.repository.bids;

import io.codefresh.gradleexample.dao.entities.bids.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BidRepository extends JpaRepository<Bid, UUID> {
    List<Bid> findBidsByAuthorId(UUID bidderId);
    List<Bid> findBidsByTenderId(UUID tenderId);
    boolean existsBidByIdAndAuthorId(UUID bidId, UUID tenderId);
    List<Bid> findByTenderIdAndAuthorId(UUID tenderId, UUID authorId);
}
