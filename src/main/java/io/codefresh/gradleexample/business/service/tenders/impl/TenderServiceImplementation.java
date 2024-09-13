package io.codefresh.gradleexample.business.service.tenders.impl;

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
import io.codefresh.gradleexample.exceptions.service.tenders.TenderNotFoundException;
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
    private static final Logger logger = LoggerFactory.getLogger(TenderServiceImplementation.class);

    private final TenderRepository tenderRepository;

    private final TenderBuilder tenderBuilder;

    private final TenderResponsibleServiceInterface tenderResponsibleService;
    private final UserServiceInterface userService;
    private final TenderHistoryServiceInterface tenderHistoryService;

    @Autowired
    public TenderServiceImplementation(TenderBuilder tenderBuilder, TenderRepository TenderRepository, TenderResponsibleServiceInterface tenderResponsibleService, UserServiceInterface userService, TenderHistoryServiceInterface tenderHistoryService) {
        this.tenderBuilder = tenderBuilder;
        this.tenderRepository = TenderRepository;
        this.tenderResponsibleService = tenderResponsibleService;
        this.userService = userService;
        this.tenderHistoryService = tenderHistoryService;
    }

    @Override
    public List<TenderDTO> getAllTenders(Integer limit, Integer offset, List<String> serviceTypes) {
        if (!isValidEnumValue(serviceTypes, ServiceTypes.class)) {
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
    public TenderDTO createTender(String name, String description, ServiceTypes serviceType, UUID organization_id, String creatorUsername) {
        UUID userID = userService.getEmployeeIdByUsername(creatorUsername);

        if (userID == null) {
            throw new EmployeeNotFoundException("Пользователь не существует или некорректен.");
        }

        if (!tenderResponsibleService.hasResponsible(organization_id, userID)){
            throw new EmployeeHasNoResponsibleException("Недостаточно прав для выполнения действия.");
        }

        try{

            tenderBuilder
                    .name(name)
                    .description(description)
                    .serviceType(serviceType)
                    .organization_id(organization_id)
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
    public TenderStatuses tenderStatuses(UUID tenderID, String username) {

        Optional<Tender> tender = tenderRepository.findById(tenderID);
        if (!tender.isPresent()) {
            throw new TenderNotFoundException("Тендер не найден.");
        }

        if (!userService.isEmployeeExistByUsername(username)){
            throw new EmployeeNotFoundException("Пользователь не существует или некорректен.");
        }

        if (!Objects.equals(tender.get().getCreatorUsername(), username)){
            throw new EmployeeHasNoResponsibleException("Недостаточно прав для выполнения действия.");
        }

        return tender.get().getStatus();
    }

    @Override
    public TenderDTO changeTenderStatus(UUID tenderID, String newStatus, String username) {

        if (!isValidEnumValue(new ArrayList<>(Collections.singleton(newStatus)), TenderStatuses.class)){
            throw new InvalidEnumException("Неверный формат запроса или его параметры.");
        }

        Optional<Tender> tender = tenderRepository.findById(tenderID);
        if (!tender.isPresent()) {
            throw new TenderNotFoundException("Тендер не найден.");
        }

        UUID userId = userService.getEmployeeIdByUsername(username);

        if (userId == null){
            throw new EmployeeNotFoundException("Пользователь не существует или некорректен.");
        }

        if (!tenderResponsibleService.hasResponsible(tender.get().getOrganization_id(), userId)){
            throw new EmployeeHasNoResponsibleException("Недостаточно прав для выполнения действия.");
        }

        tender.get().setStatus(TenderStatuses.valueOf(newStatus));
        tenderRepository.save(tender.get());
        return TenderConverter.toDTO(tender.get());
    }

    @Transactional
    @Override
    public TenderDTO editTender(UUID tenderId, String username, Map<String, Object> updates) {

        Tender tender = this.validateTender(tenderId, username);

        if (updates.containsKey("serviceType")){
            if (!isValidEnumValue(
                    Collections.singletonList(String.valueOf(updates.get("serviceType"))),
                    ServiceTypes.class)){
                throw new InvalidEnumException("Данные неправильно сформированы или не соответствуют требованиям.");
            }
        }

        if (updates.containsKey("status")){
            if (!isValidEnumValue(
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
    public TenderDTO rollbackTender(UUID tenderID, Integer targetVersion, String username) {
        this.validateTender(tenderID, username);
        return tenderHistoryService.rollbackTender(tenderID, targetVersion, username);
    }

    private <T extends Enum<T>> boolean isValidEnumValue(List<String> values,
                                                         Class<T> enumClass) {
        for (String value : values) {
            boolean found = false;
            for (T enumConstant : enumClass.getEnumConstants()) {
                if (enumConstant.name().equals(value)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    private Tender validateTender(UUID tenderID, String username){
        Optional<Tender> existingTender = tenderRepository.findById(tenderID);
        if (!existingTender.isPresent()){
            throw new TenderNotFoundException("Тендер не найден.");
        }

        Tender tender = existingTender.get();

        UUID userId = userService.getEmployeeIdByUsername(username);
        if (userId == null) {
            throw new EmployeeNotFoundException("Пользователь не существует или некорректен.");
        }
        if (!tenderResponsibleService.hasResponsible(tender.getOrganization_id(), userId)) {
            throw new EmployeeHasNoResponsibleException("Недостаточно прав для выполнения действия.");
        }
        return tender;
    }
}
