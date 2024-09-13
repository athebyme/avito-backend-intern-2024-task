package io.codefresh.gradleexample.dao.entities.tenders;

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
public class TenderHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tender_id", nullable = false)
    private Tender tender;

    private Integer version;
    private String description;
    private String name;
    private String creatorUsername;

    @Enumerated(EnumType.STRING)
    private TenderStatuses status;

    @Enumerated(EnumType.STRING)
    private ServiceTypes serviceType;

    private Timestamp created_at;

    public TenderHistory(Tender tender) {
        this.tender = tender;
        this.version = tender.getVersion();
        this.description = tender.getDescription();
        this.name = tender.getName();
        this.status = tender.getStatus();
        this.serviceType = tender.getService_type();
        this.created_at = new Timestamp(System.currentTimeMillis());
        this.creatorUsername = tender.getCreatorUsername();
    }
}
