package io.codefresh.gradleexample.business.service;

import java.util.UUID;

public interface TenderResponsibleServiceInterface {
    boolean hasResponsible(UUID organization_id, UUID user_id);
}
