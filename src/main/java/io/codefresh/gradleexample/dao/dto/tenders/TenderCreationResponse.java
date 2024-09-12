package io.codefresh.gradleexample.dao.dto.tenders;


import io.codefresh.gradleexample.dao.entities.tenders.ServiceTypes;
import io.codefresh.gradleexample.exceptions.dto_exceptions.InvalidDescriptionException;
import io.codefresh.gradleexample.exceptions.dto_exceptions.InvalidIdException;
import io.codefresh.gradleexample.exceptions.dto_exceptions.InvalidUsernameException;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TenderCreationResponse {
    private UUID organizationId;
    private String creatorUsername;
    private String description;
    private String name;
    ServiceTypes serviceType;


    public TenderCreationResponse(
            String description,
            String name,
            String creatorUsername,
            UUID organizationId,
            ServiceTypes serviceType){
        if (name == null || creatorUsername == null){throw new InvalidUsernameException("name cannot be null");}
        if (description == null) {throw new InvalidDescriptionException("description cannot be null");}
        if (organizationId == null){throw new InvalidIdException("organization_id cannot be null");}

        this.description = description;
        this.name = name;
        this.creatorUsername = creatorUsername;
        this.organizationId = organizationId;
        this.serviceType = serviceType;
    }
}
