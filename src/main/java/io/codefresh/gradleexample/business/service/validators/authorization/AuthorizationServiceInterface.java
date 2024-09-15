package io.codefresh.gradleexample.business.service.validators.authorization;

import java.util.UUID;

public interface AuthorizationServiceInterface {
    void checkUserOrganizationResponses(UUID organizationId, UUID userId);
    void checkUserBidResponses(UUID bidId, UUID userId);
}
