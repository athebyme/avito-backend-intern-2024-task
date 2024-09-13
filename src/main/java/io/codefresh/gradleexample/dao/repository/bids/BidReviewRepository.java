package io.codefresh.gradleexample.dao.repository.bids;

import io.codefresh.gradleexample.dao.entities.bids.BidReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BidReviewRepository extends JpaRepository<BidReview, UUID> {
}
