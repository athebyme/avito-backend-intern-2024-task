package io.codefresh.gradleexample.dao.dto.bids;

import io.codefresh.gradleexample.dao.converters.bids.BidConverter;
import io.codefresh.gradleexample.dao.entities.bids.BidReview;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
public class BidReviewDTO {
    private UUID id;
    private String description;
    private Timestamp createdAt;

    private BidDTO bid;

    public BidReviewDTO(BidReview bidReview) {
        this.id = bidReview.getId();
        this.description = bidReview.getDescription();
        this.createdAt = bidReview.getCreatedAt();
        this.bid = BidConverter.toDTO(bidReview.getBid());
    }
}