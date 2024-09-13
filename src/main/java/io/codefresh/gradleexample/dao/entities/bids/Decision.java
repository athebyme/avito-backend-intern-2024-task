package io.codefresh.gradleexample.dao.entities.bids;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "decisions")
public class Decision {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "bid_id")
    private Bid bid;
    private String username;
    private BidDecision decision;
    public Decision() {
    }
}