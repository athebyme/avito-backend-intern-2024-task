package io.codefresh.gradleexample.dao.entities.tenders;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Tender {
    @Id
    UUID id;
    Integer version;
    String description;
    String name;
    String creatorUsername;
    UUID organization_id;
    TenderStatuses tender_status;
    ServiceTypes service_type;

    public Tender(
            UUID id,
            Integer version,
            String description,
            String name,
            String creatorUsername,
            UUID organization_id,
            TenderStatuses tender_status) {
//        if (id == null){throw new InvalidIdException("id cannot be null");}
//        if (version == null){throw new InvalidVersionException("version cannot be null");}
//        if (name == null || creatorUsername == null){throw new InvalidUsernameException("name cannot be null");}
//        if (description == null) {throw new InvalidDescriptionException("description cannot be null");}
//        if (organization_id == null){throw new InvalidIdException("organization_id cannot be null");}

        this.id = id;
        this.version = version;
        this.description = description;
        this.name = name;
        this.creatorUsername = creatorUsername;
        this.organization_id = organization_id;
        this.tender_status = tender_status;
    }

    public Tender() {}
}
