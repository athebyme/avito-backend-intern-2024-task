package io.codefresh.gradleexample.business.service.tenders.impl;

import io.codefresh.gradleexample.business.service.validators.authorization.AuthorizationServiceInterface;
import io.codefresh.gradleexample.business.service.validators.values.impl.ValidationService;
import io.codefresh.gradleexample.business.service.users.UserServiceInterface;
import io.codefresh.gradleexample.business.service.tenders.TenderHistoryServiceInterface;
import io.codefresh.gradleexample.business.service.tenders.TenderResponsibleServiceInterface;
import io.codefresh.gradleexample.business.service.tenders.TenderServiceInterface;
import io.codefresh.gradleexample.dao.builders.tender.TenderBuilder;
import io.codefresh.gradleexample.dao.converters.TenderConverter;
import io.codefresh.gradleexample.dao.dto.tenders.TenderDTO;
import io.codefresh.gradleexample.dao.entities.tenders.ServiceTypes;
import io.codefresh.gradleexample.dao.entities.tenders.Tender;
import io.codefresh.gradleexample.dao.entities.tenders.TenderStatuses;
import io.codefresh.gradleexample.dao.repository.tenders.TenderRepository;
import io.codefresh.gradleexample.exceptions.service.employee.EmployeeHasNoResponsibleException;
import io.codefresh.gradleexample.exceptions.service.employee.EmployeeNotFoundException;
import io.codefresh.gradleexample.exceptions.service.InvalidEnumException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TenderServiceImplementation implements TenderServiceInterface {
    private final Logger logger = LoggerFactory.getLogger(TenderServiceImplementation.class);
    private final ValidationService validationService;
    private final AuthorizationServiceInterface authorizationService;

    private final TenderRepository tenderRepository;

    private final TenderBuilder tenderBuilder;

    private final TenderResponsibleServiceInterface tenderResponsibleService;
    private final UserServiceInterface userService;
    private final TenderHistoryServiceInterface tenderHistoryService;

    @Autowired
    public TenderServiceImplementation(ValidationService validationService, AuthorizationServiceInterface authorizationService, TenderBuilder tenderBuilder, TenderRepository TenderRepository, TenderResponsibleServiceInterface tenderResponsibleService, UserServiceInterface userService, TenderHistoryServiceInterface tenderHistoryService) {
        this.validationService = validationService;
        this.authorizationService = authorizationService;
        this.tenderBuilder = tenderBuilder;
        this.tenderRepository = TenderRepository;
        this.tenderResponsibleService = tenderResponsibleService;
        this.userService = userService;
        this.tenderHistoryService = tenderHistoryService;
    }

    @Override
    public List<TenderDTO> getAllTenders(Integer limit, Integer offset, List<String> serviceTypes) {
        if (!validationService.isValidEnumValue(serviceTypes, ServiceTypes.class)) {
            throw new InvalidEnumException("Неверный формат запроса или его параметры.");
        }

        List<Tender> tenders = tenderRepository.findAll();

        if (offset != null){
            tenders = tenders.subList(offset, tenders.size());
        }

        if (limit != null){
            tenders = tenders.subList(0, limit);
        }

        if (!serviceTypes.isEmpty()) {
            tenders = tenders.stream()
                    .filter(tender -> serviceTypes.contains(tender.getService_type().name()))
                    .collect(Collectors.toList());
        }

        return tenders.stream()
                .map(TenderConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TenderDTO> getTendersByUsername(Integer limit, Integer offset, String username) {
        if (!userService.isEmployeeExistByUsername(username)){
            throw new EmployeeNotFoundException("Пользователь не существует или некорректен.");
        }

        List<Tender> entities = tenderRepository.findByCreatorUsername(username);

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
    public TenderDTO createTender(String name, String description, ServiceTypes serviceType, String organization_id, String creatorUsername) {
        UUID userID = validationService.checkUserExist(creatorUsername);

        validationService.checkUUID(organization_id);

        if (userID == null) {
            throw new EmployeeNotFoundException("Пользователь не существует или некорректен.");
        }

        if (!tenderResponsibleService.hasResponsible(UUID.fromString(organization_id), userID)){
            throw new EmployeeHasNoResponsibleException("Недостаточно прав для выполнения действия.");
        }

        try{

            tenderBuilder
                    .name(name)
                    .description(description)
                    .serviceType(serviceType)
                    .organization_id(UUID.fromString(organization_id))
                    .creatorName(creatorUsername)
                    .version(1)
                    .status(TenderStatuses.Created);
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
    public TenderStatuses tenderStatuses(String tenderID, String username) {
        Tender tender = validationService.checkTenderExists(tenderID);
        UUID userID = validationService.checkUserExist(username);
        authorizationService.checkUserOrganizationResponses(tender.getOrganization_id(), userID);

        return tender.getStatus();
    }

    @Override
    public TenderDTO changeTenderStatus(String tenderID, String newStatus, String username) {

        if (!validationService.isValidEnumValue(new ArrayList<>(Collections.singleton(newStatus)), TenderStatuses.class)){
            throw new InvalidEnumException("Неверный формат запроса или его параметры.");
        }

        Tender tender = validationService.checkTenderExists(tenderID);
        UUID userId = validationService.checkUserExist(username);
        authorizationService.checkUserOrganizationResponses(tender.getOrganization_id(), userId);

        tender.setStatus(TenderStatuses.valueOf(newStatus));
        tenderRepository.save(tender);
        return TenderConverter.toDTO(tender);
    }

    @Transactional
    @Override
    public TenderDTO editTender(String tenderId, String username, Map<String, Object> updates) {

        Tender tender = validateTenderExistenceAndUserResponses(tenderId, username);

        if (updates.containsKey("serviceType")){
            if (!validationService.isValidEnumValue(
                    Collections.singletonList(String.valueOf(updates.get("serviceType"))),
                    ServiceTypes.class)){
                throw new InvalidEnumException("Данные неправильно сформированы или не соответствуют требованиям.");
            }
        }

        if (updates.containsKey("status")){
            if (!validationService.isValidEnumValue(
                    Collections.singletonList(String.valueOf(updates.get("status"))),
                    TenderStatuses.class)){
                throw new InvalidEnumException("Данные неправильно сформированы или не соответствуют требованиям.");
            }
        }

        tenderHistoryService.saveTenderHistory(tender);

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();

            //как будто бы можно сделать цепочку ответственностей
            switch (field) {
                case "name":
                    tender.setName((String) value);
                    break;
                case "description":
                    tender.setDescription((String) value);
                    break;
                case "serviceType":
                    ServiceTypes serviceType = ServiceTypes.valueOf((String) value);
                    tender.setService_type(serviceType);
                    break;
                case "creatorUsername":
                    tender.setCreatorUsername((String) value);
                    break;
                case "organization_id":
                    if (value instanceof String) {
                        tender.setOrganization_id(UUID.fromString((String) value));
                    } else if (value instanceof UUID) {
                        tender.setOrganization_id((UUID) value);
                        break;
                    }
                case "status":
                    TenderStatuses status = TenderStatuses.valueOf((String) value);
                    tender.setStatus(status);
                    break;
                case "created_at":
                    tender.setCreated_at(Timestamp.valueOf((String) value));
                    break;
                case "updated_at":
                    tender.setUpdated_at(Timestamp.valueOf((String) value));
                    break;
                default:
                    throw new InvalidEnumException("Данные неправильно сформированы или не соответствуют требованиям.");
            }
        }

        tender.setVersion(tender.getVersion() + 1);
        tender.setUpdated_at(new Timestamp(System.currentTimeMillis()));
        tenderRepository.save(tender);

        return TenderConverter.toDTO(tender);
    }

    @Override
    public TenderDTO rollbackTender(String tenderID, Integer targetVersion, String username) {
        validateTenderExistenceAndUserResponses(tenderID, username);
        return tenderHistoryService.rollbackTender(UUID.fromString(tenderID), targetVersion, username);
    }

    @Override
    public Tender getTenderByTenderId(String tenderID) {
        return validationService.checkTenderExists(tenderID);
    }

    @Override
    public boolean checkTenderExists(String tenderID) {
        return validationService.checkTenderExists(tenderID) != null;
    }


    private Tender validateTenderExistenceAndUserResponses(String tenderID, String username) {
        Tender tender = validationService.checkTenderExists(tenderID);
        UUID userId = validationService.checkUserExist(username);
        authorizationService.checkUserOrganizationResponses(tender.getOrganization_id(), userId);
        return tender;
    }
}
