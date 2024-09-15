package io.codefresh.gradleexample.business.service.review;

import io.codefresh.gradleexample.dao.entities.bids.BidReview;

import java.util.List;

public interface BidReviewServiceInterface {
    List<BidReview> getBidReviews(String tenderIdStr, String authorUsername, String requesterUsername, int limit, int offset);
}
