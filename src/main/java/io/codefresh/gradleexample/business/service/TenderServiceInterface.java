package io.codefresh.gradleexample.business.service;

import io.codefresh.gradleexample.dao.dto.tenders.TenderDTO;
import io.codefresh.gradleexample.dao.entities.tenders.ServiceTypes;
import io.codefresh.gradleexample.dao.entities.tenders.TenderStatuses;

import java.util.List;
import java.util.UUID;

public interface TenderServiceInterface {
    List<TenderDTO> getAllTenders(Integer limit, Integer offset, ServiceTypes serviceType);
    List<TenderDTO> getTendersByUsername(Integer limit, Integer offset, String username);
    TenderDTO createTender(String name,
                           String description,
                           ServiceTypes serviceType,
                           UUID organization_id,
                           String creatorUsername);
    TenderStatuses tenderStatuses(UUID tenderID, String username);
}
