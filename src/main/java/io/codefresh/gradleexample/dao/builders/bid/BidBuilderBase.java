package io.codefresh.gradleexample.dao.builders.bid;

import io.codefresh.gradleexample.dao.builders.IBuilder;
import io.codefresh.gradleexample.dao.entities.bids.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public abstract class BidBuilderBase implements IBuilder<Bid> {
    private UUID id;
    private UUID tenderId;
    private String name;
    private String description;
    private AuthorType authorType;
    private List<Decision> decision;
    private BidDecision decisionStatus;
    private BidsStatuses status;
    private UUID authorId;
    private Integer version;
    private BidReview review;
    private String reviewDescription;
    private Timestamp created_at;
    private Timestamp updated_at;

    public BidBuilderBase id(UUID id) {
        this.id = id;
        return this;
    }
    public BidBuilderBase name(String name) {
        this.name = name;
        return this;
    }
    public BidBuilderBase description(String description) {
        this.description = description;
        return this;
    }
    public BidBuilderBase authorType(AuthorType authorType) {
        this.authorType = authorType;
        return this;
    }
    public BidBuilderBase decision(List<Decision> decision) {
        this.decision = decision;
        return this;
    }
    public BidBuilderBase decisionStatus(BidDecision decisionStatus) {
        this.decisionStatus = decisionStatus;
        return this;
    }
    public BidBuilderBase status(BidsStatuses status) {
        this.status = status;
        return this;
    }
    public BidBuilderBase authorId(UUID authorId) {
        this.authorId = authorId;
        return this;
    }
    public BidBuilderBase version(Integer version) {
        this.version = version;
        return this;
    }
    public BidBuilderBase review(BidReview review) {
        this.review = review;
        return this;
    }
    public BidBuilderBase reviewDescription(String reviewDescription) {
        this.reviewDescription = reviewDescription;
        return this;
    }

    public BidBuilderBase created_at() {
        this.created_at = new Timestamp(System.currentTimeMillis());
        return this;
    }
    public BidBuilderBase tenderId(UUID tenderId) {
        this.tenderId = tenderId;
        return this;
    }

    public Bid Build(){
        return new Bid(
                null,
                this.name,
                this.description,
                this.authorType,
                this.decision,
                this.decisionStatus,
                this.status,
                this.authorId,
                this.tenderId,
                this.version,
                this.review,
                this.reviewDescription,
                this.created_at,
                this.updated_at
        );
    }

}
