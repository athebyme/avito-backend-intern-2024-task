package io.codefresh.gradleexample.business.service.implementation.tender;

import io.codefresh.gradleexample.business.service.TenderResponsibleServiceInterface;
import io.codefresh.gradleexample.business.service.TenderServiceInterface;
import io.codefresh.gradleexample.business.service.UserServiceInterface;
import io.codefresh.gradleexample.dao.builders.TenderBuilder;
import io.codefresh.gradleexample.dao.converters.TenderConverter;
import io.codefresh.gradleexample.dao.dto.tenders.TenderDTO;
import io.codefresh.gradleexample.dao.entities.tenders.ServiceTypes;
import io.codefresh.gradleexample.dao.entities.tenders.Tender;
import io.codefresh.gradleexample.dao.entities.tenders.TenderStatuses;
import io.codefresh.gradleexample.dao.repository.TenderRepository;
import io.codefresh.gradleexample.exceptions.service.EmployeeHasNoResponsibleException;
import io.codefresh.gradleexample.exceptions.service.EmployeeNotFoundException;
import io.codefresh.gradleexample.exceptions.service.TenderNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TenderServiceImplementation implements TenderServiceInterface {
    private static final Logger logger = LoggerFactory.getLogger(TenderServiceImplementation.class);

    private final TenderBuilder tenderBuilder;
    private final TenderRepository tenderRepository;
    private final TenderResponsibleServiceInterface tenderResponsibleService;
    private final UserServiceInterface userService;

    @Autowired
    public TenderServiceImplementation(TenderBuilder tenderBuilder, TenderRepository TenderRepository, TenderResponsibleServiceInterface tenderResponsibleService, UserServiceInterface userService) {
        this.tenderBuilder = tenderBuilder;
        this.tenderRepository = TenderRepository;
        this.tenderResponsibleService = tenderResponsibleService;
        this.userService = userService;
    }

    @Override
    public List<TenderDTO> getAllTenders(Integer limit, Integer offset, ServiceTypes serviceType) {
        List<Tender> tenders = tenderRepository.findAll();

        if (offset != null){
            tenders = tenders.subList(offset, tenders.size());
        }

        if (limit != null){
            tenders = tenders.subList(0, limit);
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
        List<Tender> entities = tenderRepository.findByCreatorUsername(username);
        if (entities == null) {
            throw new TenderNotFoundException(String.format("%s not found", username));
        }
        if (offset != null){
            if (offset > entities.size()) {
                entities = new ArrayList<>();
            } else {
                entities = entities.subList(offset, entities.size());
            }
        }

        if (limit != null){
            int endIndex = Math.min(limit, entities.size());
            entities = entities.subList(0, endIndex);
        }
        return entities.stream()
                .map(TenderConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TenderDTO createTender(String name, String description, ServiceTypes serviceType, UUID organization_id, String creatorUsername) {
        UUID userID = userService.getEmployeeIdByUsername(creatorUsername);

        if (userID == null) {
            throw new EmployeeNotFoundException("Employee not found");
        }

        if (!tenderResponsibleService.hasResponsible(organization_id, userID)){
            throw new EmployeeHasNoResponsibleException("Employee not found");
        }
        try{

            // использовать билдер !!!!
            tenderBuilder
                    .name(name)
                    .description(description)
                    .serviceType(serviceType)
                    .organization_id(organization_id)
                    .creatorName(creatorUsername)
                    .version(1)
                    .status(TenderStatuses.CREATED);
        }
        catch (Exception e){
            logger.error(e.getMessage());
        }

        Tender tender = tenderBuilder.Build();
        tender.setCreated_at(new Timestamp(System.currentTimeMillis()));

        tenderRepository.save(tender);
        return TenderConverter.toDTO(tender);
    }

    @Override
    public TenderStatuses tenderStatuses(UUID tenderID, String username) {
        // responsible check
        Tender tender = tenderRepository.findByCreatorUsername(username).stream().filter(p -> p.getId().equals(tenderID)).findFirst().orElse(null);
        if (tender == null) {
            throw new TenderNotFoundException(String.format("%s not found", tenderID));
        }
        return tender.getStatus();
    }
}
