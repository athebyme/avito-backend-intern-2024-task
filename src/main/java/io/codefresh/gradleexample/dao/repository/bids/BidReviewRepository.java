package io.codefresh.gradleexample.dao.repository.bids;

import io.codefresh.gradleexample.dao.entities.bids.Bid;
import io.codefresh.gradleexample.dao.entities.bids.BidReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BidReviewRepository extends JpaRepository<BidReview, UUID> {
    List<BidReview> findByBidIn(List<Bid> bids);
}
