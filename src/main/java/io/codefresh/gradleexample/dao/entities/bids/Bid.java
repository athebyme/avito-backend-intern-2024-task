package io.codefresh.gradleexample.dao.entities.bids;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "bids")
@AllArgsConstructor
public class Bid implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private AuthorType authorType;

    @OneToMany(mappedBy = "bid")
    private List<Decision> decision;

    @Enumerated(EnumType.STRING)
    private BidDecision decisionStatus;

    @Enumerated(EnumType.STRING)
    private BidsStatuses status;

    @Column(name = "author_id")
    private UUID authorId;


    private UUID tenderId;

    private Integer version;

    @OneToOne(mappedBy = "bid", cascade = CascadeType.ALL)
    private BidReview review;

    private String reviewDescription;

    private Timestamp created_at;
    private Timestamp updated_at;

    public Bid() {
    }

    @Override
    public Bid clone() {
        try {
            return (Bid) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Ошибка клонирования Bid", e);
        }
    }
}
