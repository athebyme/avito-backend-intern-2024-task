CREATE TABLE tender (
        id UUID PRIMARY KEY,
        name VARCHAR(100) NOT NULL,
        description VARCHAR(500) NOT NULL,
        service_type VARCHAR(50) NOT NULL,
        status VARCHAR(50) NOT NULL,
        organization_id UUID NOT NULL,
        version INT NOT NULL DEFAULT 1,
        created_at TIMESTAMP NOT NULL,
        FOREIGN KEY (organization_id) REFERENCES organization(id)
);