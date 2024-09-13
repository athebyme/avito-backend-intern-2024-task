package io.codefresh.gradleexample.dao.builders.tender;

import io.codefresh.gradleexample.dao.builders.IBuilder;
import io.codefresh.gradleexample.dao.entities.tenders.ServiceTypes;
import io.codefresh.gradleexample.dao.entities.tenders.Tender;
import io.codefresh.gradleexample.dao.entities.tenders.TenderStatuses;

import java.util.UUID;

public abstract class TenderBuilderBase implements IBuilder<Tender> {
    private Integer version;
    private String description;
    private String name;
    private String creatorUsername;
    private UUID organization_id;
    private TenderStatuses status;
    private ServiceTypes service_type;

    public TenderBuilderBase organization_id(UUID organization_id){
        this.organization_id = organization_id;
        return this;
    }

    public TenderBuilderBase version(Integer version){
        this.version = version;
        return this;
    }

    public TenderBuilderBase description(String description){
        this.description = description;
        return this;
    }

    public TenderBuilderBase name(String name){
        this.name = name;
        return this;
    }

    public TenderBuilderBase creatorName(String creatorName){
        this.creatorUsername = creatorName;
        return this;
    }

    public TenderBuilderBase status(TenderStatuses status){
        this.status = status;
        return this;
    }

    public TenderBuilderBase serviceType(ServiceTypes serviceType){
        this.service_type = serviceType;
        return this;
    }

    public Tender Build(){
        return new Tender(
                null,
                this.version,
                this.description,
                this.name,
                this.creatorUsername,
                this.organization_id,
                this.status,
                this.service_type,
                null
        );
    }

}
