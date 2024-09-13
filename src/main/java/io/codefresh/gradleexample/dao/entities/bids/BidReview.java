package io.codefresh.gradleexample.dao.entities.bids;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "bid_review")
public class BidReview {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String description;
    private Timestamp createdAt;
    @OneToOne
    @JoinColumn(name = "bid_id", referencedColumnName = "id")
    private Bid bid;
}
