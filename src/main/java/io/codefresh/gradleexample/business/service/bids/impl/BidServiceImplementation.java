package io.codefresh.gradleexample.business.service.bids.impl;

import io.codefresh.gradleexample.business.service.bids.BidHistoryServiceInterface;
import io.codefresh.gradleexample.business.service.bids.BidServiceInterface;
import io.codefresh.gradleexample.business.service.tenders.TenderResponsibleServiceInterface;
import io.codefresh.gradleexample.business.service.users.UserServiceInterface;
import io.codefresh.gradleexample.dao.builders.bid.BidBuilderBase;
import io.codefresh.gradleexample.dao.converters.bids.BidConverter;
import io.codefresh.gradleexample.dao.dto.EmployeeDTO;
import io.codefresh.gradleexample.dao.dto.bids.BidDTO;
import io.codefresh.gradleexample.dao.entities.bids.*;
import io.codefresh.gradleexample.dao.entities.tenders.Tender;
import io.codefresh.gradleexample.dao.entities.users.Employee;
import io.codefresh.gradleexample.dao.repository.bids.BidRepository;
import io.codefresh.gradleexample.dao.repository.bids.BidReviewRepository;
import io.codefresh.gradleexample.dao.repository.bids.DecisionRepository;
import io.codefresh.gradleexample.dao.repository.tenders.TenderRepository;
import io.codefresh.gradleexample.exceptions.service.InvalidEnumException;
import io.codefresh.gradleexample.exceptions.service.InvalidUUIDException;
import io.codefresh.gradleexample.exceptions.service.bids.BidNotFoundException;
import io.codefresh.gradleexample.exceptions.service.bids.ReviewAlreadySentException;
import io.codefresh.gradleexample.exceptions.service.bids.UserAlreadySentDecisionException;
import io.codefresh.gradleexample.exceptions.service.employee.EmployeeHasNoResponsibleException;
import io.codefresh.gradleexample.exceptions.service.employee.EmployeeNotFoundException;
import io.codefresh.gradleexample.exceptions.service.tenders.TenderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BidServiceImplementation implements BidServiceInterface {

    private final BidRepository bidRepository;
    private final TenderRepository tenderRepository;
    private final UserServiceInterface userService;
    private final TenderResponsibleServiceInterface tenderResponsibleService;
    private final BidHistoryServiceInterface bidHistoryServiceInterface;
    private final DecisionRepository decisionRepository;
    private final BidReviewRepository bidReviewRepository;

    private final BidBuilderBase bidBuilder;

    @Autowired
    public BidServiceImplementation(
            BidRepository bidRepository,
            TenderRepository tenderRepository,
            UserServiceInterface userService,
            TenderResponsibleServiceInterface tenderResponsibleService,
            BidHistoryServiceInterface bidHistoryServiceInterface,
            DecisionRepository decisionRepository,
            BidReviewRepository bidReviewRepository,
            BidBuilderBase bidBuilder) {
        this.bidRepository = bidRepository;
        this.tenderRepository = tenderRepository;
        this.userService = userService;
        this.tenderResponsibleService = tenderResponsibleService;
        this.bidHistoryServiceInterface = bidHistoryServiceInterface;
        this.decisionRepository = decisionRepository;
        this.bidReviewRepository = bidReviewRepository;
        this.bidBuilder = bidBuilder;
    }

    @Override
    public BidDTO createBid(String name, String description, String tenderId, String authorType, String authorId) {
        String authorUsername = userService.getEmployeeById(UUID.fromString(authorId)).getUsername();
        validate(UUID.fromString(tenderId), authorUsername);
        bidBuilder.name(name)
                .description(description)
                .authorType(AuthorType.valueOf(authorType))
                .authorId(UUID.fromString(authorId))
                .version(1)
                .status(BidsStatuses.Created)
                .tenderId(UUID.fromString(tenderId))
                .created_at();
        Bid bid = bidBuilder.Build();
        bidRepository.save(bid);
        return BidConverter.toDTO(bid);
    }

    @Override
    public List<BidDTO> getBidsByUsername(Integer limit, Integer offset, String username) {
        UUID userID = userService.getEmployeeIdByUsername(username);
        if (userID == null){
            throw new EmployeeNotFoundException("Пользователь не существует или некорректен.");
        }
        List<Bid> entities = bidRepository.findBidsByAuthorId(userID);
        List<BidDTO> sortedEntities = entities.stream()
                .map(BidConverter::toDTO)
                .sorted(Comparator.comparing(BidDTO::getName))  // Сортировка по полю name
                .collect(Collectors.toList());

        if (offset != null){
            if (offset > entities.size()) {
                sortedEntities = new ArrayList<>();
            } else {
                sortedEntities = sortedEntities.subList(offset, entities.size());
            }
        }

        if (limit != null){
            int endIndex = Math.min(limit, sortedEntities.size());
            sortedEntities = sortedEntities.subList(0, endIndex);
        }
        return sortedEntities;
    }

    @Override
    public List<BidDTO> getTenderBids(Integer limit, Integer offset, String username, String tenderId) {
        checkUUID(tenderId);
        validate(UUID.fromString(tenderId), username);
        UUID userID = userService.getEmployeeIdByUsername(username);
        if (bidRepository.findBidsByAuthorId(userID).isEmpty()){
            throw new BidNotFoundException("Тендер или предложение не найдено.");
        }
        Tender tender = tenderRepository.findByCreatorUsername(username).stream().filter(p -> Objects.equals(p.getId(), UUID.fromString(tenderId))).findFirst().orElse(null);
        if (tender == null){
            throw new TenderNotFoundException("Тендер или предложение не найдено.");
        }

        List<Bid> bids = bidRepository.findBidsByTenderId(UUID.fromString(tenderId));
        if (bids.isEmpty()) {
            throw new BidNotFoundException("Предложения не найдены");
        }

        bids.sort(Comparator.comparing(Bid::getName));

        if (offset != null){
            if (offset > bids.size()) {
                bids = new ArrayList<>();
            } else {
                bids = bids.subList(offset, bids.size());
            }
        }

        if (limit != null){
            int endIndex = Math.min(limit, bids.size());
            bids = bids.subList(0, endIndex);
        }

        return bids.stream()
                .map(BidConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BidsStatuses getBidsStatuses(String bidId, String username) {
        UUID userId = checkUserExist(username);
        checkUUID(bidId);
        Bid bid = bidRepository.findById(UUID.fromString(bidId)).orElse(null);

        checkUserExist(username);
        checkUserResponsesBid(UUID.fromString(bidId), userId);
        if (bid == null){
            throw new BidNotFoundException("Предложение не найдено.");
        }
        return bid.getStatus();
    }

    @Override
    public BidDTO updateBidStatus(String bidId, String status, String username) {
        UUID bidID = checkUUID(bidId);
        checkUserExist(username);

        Bid bid = bidRepository.findById(bidID).orElse(null);

        if (bid == null){
            throw new BidNotFoundException("Предложение не найдено.");
        }
        checkUserResponsesBid(bidID, username);


        if (!isValidEnumValue(status, BidsStatuses.class)){
            throw new InvalidEnumException("Неверный формат запроса или его параметры.");
        }

        bid.setStatus(BidsStatuses.valueOf(status));

        bidRepository.save(bid);

        return BidConverter.toDTO(bid);
    }

    @Override
    public BidDTO updateBid(String bidId, String username, Map<String, Object> updates) {
        checkUpdateParameters(updates);
        checkBidExists(UUID.fromString(bidId));
        checkUserExist(username);

        Bid bid = bidRepository.findById(UUID.fromString(bidId)).orElse(null);
        if (bid == null){
            throw new BidNotFoundException("Предложение не найдено.");
        }

        bidHistoryServiceInterface.saveBidHistory(bid);

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();

            //как будто бы можно сделать цепочку ответственностей
            switch (field) {
                case "name":
                    bid.setName((String) value);
                    break;
                case "description":
                    bid.setDescription((String) value);
                    break;
                case "authorType":
                    AuthorType authorType = AuthorType.valueOf((String) value);
                    bid.setAuthorType(authorType);
                    break;
                case "status":
                    BidsStatuses status = BidsStatuses.valueOf((String) value);
                    bid.setStatus(status);
                    break;
                case "author_id":
                    if (value instanceof UUID){
                        bid.setAuthorId((UUID) value);
                    }else if(value instanceof String){
                        bid.setAuthorId(UUID.fromString((String) value));
                    }
                    break;
                case "tender_id":
                    if (value instanceof UUID){
                        bid.setTenderId((UUID) value);
                    }else if(value instanceof String){
                        bid.setTenderId(UUID.fromString((String) value));
                    }
                    break;
                case "version":
                    bid.setVersion((Integer) value);
                    break;
                case "review_description":
                    bid.setReviewDescription((String) value);
                    break;
                case "created_at":
                    bid.setCreated_at((Timestamp) value);
                    break;
                case "updated_at":
                        bid.setUpdated_at((Timestamp) value);
                    break;
                default:
                    throw new InvalidEnumException("Данные неправильно сформированы или не соответствуют требованиям.");
            }
        }
        bid.setVersion(bid.getVersion() + 1);
        bid.setUpdated_at(new Timestamp(System.currentTimeMillis()));
        bidRepository.save(bid);
        return BidConverter.toDTO(bid);
    }

    @Override
    public BidDTO rollbackBid(String bidId, String username, int version) {
        checkUUID(bidId);
        checkUserExist(username);
        checkBidExists(UUID.fromString(bidId));
        checkUserResponsesBid(UUID.fromString(bidId), username);
        return bidHistoryServiceInterface.rollbackBid(UUID.fromString(bidId), version, username);
    }


    @Override
    public void submitDecision(String bidId, String decision, String username) {
        if(!isValidEnumValue(decision, BidDecision.class)){
            throw new InvalidEnumException("Решение не может быть отправлено.");
        }
        checkUUID(bidId);
        checkBidExists(UUID.fromString(bidId));
        checkUserExist(username);
        Bid bid = bidRepository.findById(UUID.fromString(bidId)).get();
        List<Decision> existingDecisions = decisionRepository.findByBidId(UUID.fromString(bidId));
        if (existingDecisions.stream().anyMatch(d -> d.getUsername().equals(username))) {
            throw new UserAlreadySentDecisionException("Пользователь уже отправил решение");
        }

        Decision newDecision = new Decision();
        newDecision.setBid(bid);
        newDecision.setDecision(BidDecision.valueOf(decision));
        newDecision.setUsername(username);
        decisionRepository.save(newDecision);

        if (existingDecisions.stream().anyMatch(d -> d.getDecision() == BidDecision.Rejected) ||
                BidDecision.valueOf(decision) == BidDecision.Rejected) {
            bid.setDecisionStatus(BidDecision.Rejected);
            bidRepository.save(bid);
            return;
        }

        int responsibleCount = 5; // допустим, у нас 5 ответственных лиц
        int quorum = Math.min(3, responsibleCount);

        long approvedCount = existingDecisions.stream()
                .filter(d -> d.getDecision() == BidDecision.Approved)
                .count();

        if (approvedCount + (BidDecision.valueOf(decision) == BidDecision.Approved ? 1 : 0) >= quorum) {
            bid.setDecisionStatus(BidDecision.Approved);
        } else {
            bid.setDecisionStatus(BidDecision.Quorum);
        }

        bidRepository.save(bid);
    }

    @Override
    public void submitFeedback(String bidId, String feedback, String username) {
        checkUUID(bidId);
        checkBidExists(UUID.fromString(bidId));
        checkUserExist(username);
        Bid bid = bidRepository.findById(UUID.fromString(bidId)).get();
        if (bid.getReview() != null) {
            throw new ReviewAlreadySentException("Отзыв уже был отправлен по этому предложению.");
        }

        BidReview review = new BidReview();
        review.setDescription(feedback);
        review.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        review.setBid(bid);

        bid.setReview(review);
        bid.setReviewDescription(review.getDescription());
        bidRepository.save(bid);
    }

    private void validate(UUID tenderID, String username){
        Optional<Tender> existingTender = tenderRepository.findById(tenderID);
        if (!existingTender.isPresent()){
            throw new TenderNotFoundException("Тендер не найден.");
        }

        Tender tender = existingTender.get();
        UUID userId = checkUserExist(username);
        checkUserResponsesOrganization(tender.getOrganization_id(), userId);
    }

    private UUID checkUserExist(String username){
        UUID userId = userService.getEmployeeIdByUsername(username);
        if (userId == null) {
            throw new EmployeeNotFoundException("Пользователь не существует или некорректен.");
        }
        return userId;
    }

    private void checkUserResponsesOrganization(UUID organizationId, UUID userId){
        if (!tenderResponsibleService.hasResponsible(organizationId, userId)) {
            throw new EmployeeHasNoResponsibleException("Недостаточно прав для выполнения действия.");
        }
    }
    private void checkUserResponsesBid(UUID bidId, UUID userId){
        if (!bidRepository.existsBidByIdAndAuthorId(bidId, userId)) {
            throw new BidNotFoundException("Предложение не найдено.");
        }
    }
    private void checkUserResponsesBid(UUID bidId, String username){
        UUID userId = userService.getEmployeeIdByUsername(username);
        if (!bidRepository.existsBidByIdAndAuthorId(bidId, userId)) {
            throw new BidNotFoundException("Недостаточно прав для выполнения действия.");
        }
    }
    private void checkBidExists(UUID bidId){
        if (!bidRepository.existsById(bidId)){
            throw new BidNotFoundException("Предложение не найдено.");
        }
    }
    private UUID checkUUID(String uuid){
        try{
            return UUID.fromString(uuid);
        }catch (IllegalArgumentException e){
            throw new InvalidUUIDException("Неверный формат запроса или его параметры.");
        }
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
    private <T extends Enum<T>> boolean isValidEnumValue(String value,
                                                         Class<T> enumClass) {
        for (T enumConstant : enumClass.getEnumConstants()) {
            if (enumConstant.name().equals(value)) {
                return true;
            }
        }
        return false;
    }

    private void checkUpdateParameters(Map<String, Object> updates){
        if (updates.containsKey("authorType")){
            if (!isValidEnumValue(String.valueOf(updates.get("authorType")), AuthorType.class)){
                throw new InvalidEnumException("Данные неправильно сформированы или не соответствуют требованиям.");
            }
        }

        if (updates.containsKey("bidDecision")){
            if (!isValidEnumValue(String.valueOf(updates.get("bidDecision")), BidDecision.class)){
                throw new InvalidEnumException("Данные неправильно сформированы или не соответствуют требованиям.");
            }
        }
        if (updates.containsKey("status")){
            if (!isValidEnumValue(String.valueOf(updates.get("status")), BidsStatuses.class)){
                throw new InvalidEnumException("Данные неправильно сформированы или не соответствуют требованиям.");
            }
        }
        if (updates.containsKey("bidId")){
            checkUUID(updates.containsKey("bidId") ? String.valueOf(updates.get("bidId")) : null);
        }
        if (updates.containsKey("authorId")){
            checkUUID(updates.containsKey("authorId") ? String.valueOf(updates.get("authorId")) : null);
        }
        if (updates.containsKey("tenderId")){
            checkUUID(updates.containsKey("tenderId") ? String.valueOf(updates.get("tenderId")) : null);
        }
        if (updates.containsKey("reviewId")){
            checkUUID(updates.containsKey("reviewId") ? String.valueOf(updates.get("reviewId")) : null);
        }
    }
}
