package io.codefresh.gradleexample.business.service.tenders.impl;

import io.codefresh.gradleexample.business.service.tenders.TenderHistoryServiceInterface;
import io.codefresh.gradleexample.dao.converters.TenderConverter;
import io.codefresh.gradleexample.dao.dto.tenders.TenderDTO;
import io.codefresh.gradleexample.dao.entities.tenders.Tender;
import io.codefresh.gradleexample.dao.entities.tenders.TenderHistory;
import io.codefresh.gradleexample.dao.repository.tenders.TenderHistoryRepository;
import io.codefresh.gradleexample.dao.repository.tenders.TenderRepository;
import io.codefresh.gradleexample.exceptions.service.tenders.TenderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.UUID;

@Service
public class TenderHistoryServiceImplementation implements TenderHistoryServiceInterface {

    private final TenderHistoryRepository tenderHistoryRepository;
    private final TenderRepository tenderRepository;

    @Autowired
    public TenderHistoryServiceImplementation(TenderHistoryRepository tenderHistoryRepository, TenderRepository tenderRepository) {
        this.tenderHistoryRepository = tenderHistoryRepository;
        this.tenderRepository = tenderRepository;
    }

    /**
     * Сохраняет текущую версию тендера в историю.
     */
    @Override
    @Transactional
    public void saveTenderHistory(Tender tender) {
        TenderHistory tenderHistory = new TenderHistory(tender);
        tenderHistoryRepository.save(tenderHistory);
    }

    /**
     * Выполняет откат тендера к указанной версии и возвращает откатанный тендер.
     */

    @Override
    @Transactional
    public TenderDTO rollbackTender(UUID tenderId, int version, String username) {
        Tender tender = tenderRepository.findById(tenderId)
                .orElseThrow(() -> new TenderNotFoundException("Тендер с ID " + tenderId + " не найден."));

        TenderHistory targetVersion = tenderHistoryRepository.findByTenderIdAndVersion(tenderId, version)
                .orElseThrow(() -> new EntityNotFoundException("Версия " + version + " для тендера " + tenderId + " не найдена."));


        Tender clonedTender = tender.clone();
        clonedTender.setDescription(targetVersion.getDescription());
        clonedTender.setName(targetVersion.getName());
        clonedTender.setService_type(targetVersion.getServiceType());
        clonedTender.setStatus(targetVersion.getStatus());
        clonedTender.setCreated_at(targetVersion.getCreated_at());
        clonedTender.setCreatorUsername(targetVersion.getCreatorUsername());

        clonedTender.setVersion(targetVersion.getVersion() + 1);

        clonedTender.setUpdated_at(new Timestamp(System.currentTimeMillis()));

        tenderRepository.save(clonedTender);

        saveTenderHistory(clonedTender);

        return TenderConverter.toDTO(clonedTender);
    }
}
