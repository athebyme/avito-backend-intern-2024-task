package io.codefresh.gradleexample.dao.dto.bids;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BidCreationDTO {
    private String name;
    private String description;
    private String tenderId;
    private String authorType;
    private String authorId;
}
