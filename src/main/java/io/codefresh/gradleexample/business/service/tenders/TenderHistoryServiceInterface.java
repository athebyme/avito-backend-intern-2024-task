package io.codefresh.gradleexample.business.service.tenders;

import io.codefresh.gradleexample.dao.dto.tenders.TenderDTO;
import io.codefresh.gradleexample.dao.entities.tenders.Tender;

import java.util.UUID;

public interface TenderHistoryServiceInterface {
    void saveTenderHistory(Tender tender);
    TenderDTO rollbackTender(UUID tenderId, int version, String username);
}
