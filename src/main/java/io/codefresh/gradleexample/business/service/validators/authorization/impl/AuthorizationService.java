package io.codefresh.gradleexample.business.service.validators.authorization.impl;

import io.codefresh.gradleexample.business.service.tenders.TenderResponsibleServiceInterface;
import io.codefresh.gradleexample.business.service.validators.authorization.AuthorizationServiceInterface;
import io.codefresh.gradleexample.dao.repository.bids.BidRepository;
import io.codefresh.gradleexample.exceptions.service.employee.EmployeeHasNoResponsibleException;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Вообще, сервис сделан для удобства.
 * Мне кажется SRP не сильно нарушается здесь.
 * Уменьшает повторение кода.
 */
@Component
public class AuthorizationService implements AuthorizationServiceInterface {
    private final TenderResponsibleServiceInterface tenderResponsibleService;

    private final BidRepository bidRepository;

    public AuthorizationService(BidRepository bidRepository, TenderResponsibleServiceInterface tenderResponsibleService) {
        this.bidRepository = bidRepository;
        this.tenderResponsibleService = tenderResponsibleService;
    }

    @Override
    public void checkUserOrganizationResponses(UUID organizationId, UUID userId) {
        if (!tenderResponsibleService.hasResponsible(organizationId, userId)) {
            throw new EmployeeHasNoResponsibleException("Недостаточно прав для выполнения действия.");
        }
    }

    @Override
    public void checkUserBidResponses(UUID bidId, UUID userId) {
        if (!bidRepository.existsBidByIdAndAuthorId(bidId, userId)) {
            throw new EmployeeHasNoResponsibleException("Недостаточно прав для выполнения действия.");
        }
    }
}
