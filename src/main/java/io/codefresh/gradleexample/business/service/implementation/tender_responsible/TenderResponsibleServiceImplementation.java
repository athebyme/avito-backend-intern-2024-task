package io.codefresh.gradleexample.business.service.implementation.tender_responsible;

import io.codefresh.gradleexample.business.service.TenderResponsibleServiceInterface;
import io.codefresh.gradleexample.dao.repository.TenderResponsibleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TenderResponsibleServiceImplementation implements TenderResponsibleServiceInterface {

    private final TenderResponsibleRepository tenderResponsibleRepository;

    @Autowired
    public TenderResponsibleServiceImplementation(TenderResponsibleRepository tenderResponsibleRepository) {
        this.tenderResponsibleRepository = tenderResponsibleRepository;
    }

    @Override
    public boolean hasResponsible(UUID organization_id, UUID user_id) {
        return tenderResponsibleRepository.existsByOrganizationIdAndEmployeeId(organization_id, user_id);
    }
}
