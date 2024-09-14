package io.codefresh.gradleexample.dao.entities.bids;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "bid_history")
public class BidHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;


    @ManyToOne
    @JoinColumn(name = "bid_id", nullable = false)
    private Bid bid;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private AuthorType authorType;

    @Enumerated(EnumType.STRING)
    private BidsStatuses status;

    private UUID authorId;

    private UUID tenderId;

    private Integer version;

    @OneToOne(mappedBy = "bid")
    private BidReview review;

    private String reviewDescription;

    private Timestamp created_at;
    private Timestamp updated_at;

    public BidHistory(Bid bid) {
        this.bid = bid;
        this.authorId = bid.getAuthorId();
        this.authorType = bid.getAuthorType();
        this.status = bid.getStatus();
        this.tenderId = bid.getTenderId();
        this.version = bid.getVersion();
        this.review = bid.getReview();
        this.reviewDescription = bid.getReviewDescription();
        this.created_at = bid.getCreated_at();
        this.updated_at = bid.getUpdated_at();
        this.name = bid.getName();
        this.description = bid.getDescription();
    }

}
