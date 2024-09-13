package io.codefresh.gradleexample.business.service.tenders;

import io.codefresh.gradleexample.dao.dto.tenders.TenderDTO;
import io.codefresh.gradleexample.dao.entities.tenders.ServiceTypes;
import io.codefresh.gradleexample.dao.entities.tenders.TenderStatuses;

import java.util.List;
import java.util.Map;

public interface TenderServiceInterface {
    List<TenderDTO> getAllTenders(Integer limit, Integer offset, List<String> serviceTypes);
    List<TenderDTO> getTendersByUsername(Integer limit, Integer offset, String username);
    TenderDTO createTender(String name,
                           String description,
                           ServiceTypes serviceType,
                           String organization_id,
                           String creatorUsername);
    TenderStatuses tenderStatuses(String tenderID, String username);
    TenderDTO changeTenderStatus(String tenderID, String newStatus, String username);
    TenderDTO editTender(String tenderID, String username, Map<String,Object> updates);
    TenderDTO rollbackTender(String tenderID, Integer version, String username);
}
