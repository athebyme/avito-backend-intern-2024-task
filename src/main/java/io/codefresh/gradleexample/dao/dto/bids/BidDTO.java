package io.codefresh.gradleexample.dao.dto.bids;

import io.codefresh.gradleexample.dao.entities.bids.AuthorType;
import io.codefresh.gradleexample.dao.entities.bids.BidsStatuses;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class BidDTO {
    private UUID id;
    private String name;
    private BidsStatuses status;
    private AuthorType authorType;
    private UUID authorID;
    private Integer version;
    private Timestamp created_at;
}
