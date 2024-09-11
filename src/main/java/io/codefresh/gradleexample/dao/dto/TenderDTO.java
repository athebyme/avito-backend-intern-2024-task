package io.codefresh.gradleexample.dao.dto;

import io.codefresh.gradleexample.dao.entities.tenders.TenderStatuses;
import io.codefresh.gradleexample.exceptions.dto_exceptions.InvalidDescriptionException;
import io.codefresh.gradleexample.exceptions.dto_exceptions.InvalidIdException;
import io.codefresh.gradleexample.exceptions.dto_exceptions.InvalidUsernameException;
import io.codefresh.gradleexample.exceptions.dto_exceptions.InvalidVersionException;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
public class TenderDTO {
    private UUID id;
    private Integer version;
    private String description;
    private String name;
    private String creatorUsername;
    private UUID organization_id;
    private TenderStatuses tender_status;
    private Timestamp created_at;

    public TenderDTO(
            UUID id,
            Integer version,
            String description,
            String name,
            String creatorUsername,
            UUID organization_id,
            TenderStatuses tender_status){
        if (id == null){throw new InvalidIdException("id cannot be null");}
        if (version == null){throw new InvalidVersionException("version cannot be null");}
        if (name == null || creatorUsername == null){throw new InvalidUsernameException("name cannot be null");}
        if (description == null) {throw new InvalidDescriptionException("description cannot be null");}
        if (organization_id == null){throw new InvalidIdException("organization_id cannot be null");}

        this.id = id;
        this.version = version;
        this.description = description;
        this.name = name;
        this.creatorUsername = creatorUsername;
        this.organization_id = organization_id;
        this.tender_status = tender_status;
    }
}
