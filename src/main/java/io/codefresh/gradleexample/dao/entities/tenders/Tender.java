package io.codefresh.gradleexample.dao.entities.tenders;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Tender implements Cloneable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;
    Integer version;
    String description;
    String name;
    String creatorUsername;
    UUID organization_id;

    @Enumerated(EnumType.STRING)
    TenderStatuses status;

    @Enumerated(EnumType.STRING)
    @Column(name = "serviceType")
    ServiceTypes service_type;

    @Column(name = "created_at")
    Timestamp created_at;

    @Column(name = "updated_at")
    Timestamp updated_at;

    public Tender(
            UUID id,
            Integer version,
            String description,
            String name,
            String creatorUsername,
            UUID organization_id,
            TenderStatuses status,
            ServiceTypes service_type,
            Timestamp created_at) {

        this.id = id;
        this.version = version;
        this.description = description;
        this.name = name;
        this.creatorUsername = creatorUsername;
        this.organization_id = organization_id;
        this.status = status;
        this.service_type = service_type;
        this.created_at = created_at;
        this.updated_at = created_at;
    }

    public Tender() {}


    @Override
    public Tender clone() {
        try {
            return (Tender) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Ошибка клонирования Tender", e);
        }
    }
}
