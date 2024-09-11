package io.codefresh.gradleexample.business.service.implemetation;

import io.codefresh.gradleexample.business.service.TenderServiceInterface;
import io.codefresh.gradleexample.dao.converters.TenderConverter;
import io.codefresh.gradleexample.dao.dto.TenderDTO;
import io.codefresh.gradleexample.dao.entities.tenders.ServiceTypes;
import io.codefresh.gradleexample.dao.entities.tenders.TenderEntity;
import io.codefresh.gradleexample.dao.entities.tenders.TenderStatuses;
import io.codefresh.gradleexample.dao.repository.TenderRepository;
import io.codefresh.gradleexample.exceptions.service.TenderNotFoundException;
import jdk.internal.net.http.common.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class TenderServiceImplementation implements TenderServiceInterface {

    private final TenderRepository tenderRepository;

    @Autowired
    public TenderServiceImplementation(TenderRepository TenderRepository) {
        this.tenderRepository = TenderRepository;
    }

    @Override
    public List<TenderDTO> getAllTenders(Integer limit, Integer offset, ServiceTypes serviceType) {
        List<TenderEntity> tenders = tenderRepository.findAll();
        if (limit != null){
            tenders = tenders.subList(0, limit);
        }
        if (offset != null){
            tenders = tenders.subList(offset, tenders.size());
        }
        if (serviceType != null){
            tenders = tenders.stream()
                    .filter(p -> p.getService_type() == serviceType)
                    .collect(Collectors.toList());
        }
        return tenders.stream()
                .map(TenderConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TenderDTO> getTendersByUsername(Integer limit, Integer offset, String username) {
        List<TenderEntity> entities = tenderRepository.findByName(username);
        if (entities == null) {
            throw new TenderNotFoundException(String.format("%s not found", username));
        }
        if (offset != null){
            entities = entities.subList(offset, entities.size());
        }

        if (limit != null){
            entities = entities.subList(0, limit);
        }
        return entities.stream()
                .map(TenderConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TenderDTO createTender(String name, String description, ServiceTypes serviceType, UUID organization_id, String creatorUsername) {
        TenderEntity tender = new TenderEntity();
        try{
            tender.setName(name);
            tender.setDescription(description);
            tender.setService_type(serviceType);
            tender.setOrganization_id(organization_id);
            tender.setCreatorUsername(creatorUsername);
        }
        catch (Exception e){
            Log.logError(e);
        }
        tender.setTender_status(TenderStatuses.CREATED);
        tenderRepository.save(tender);
        return TenderConverter.toDTO(tender);
    }

    @Override
    public TenderStatuses tenderStatuses(UUID tenderID, String username) {
        TenderEntity tender = tenderRepository.findByName(username).stream().filter(p -> p.getId().equals(tenderID)).findFirst().orElse(null);
        if (tender == null) {
            throw new TenderNotFoundException(String.format("%s not found", tenderID));
        }
        return tender.getTender_status();
    }
}
