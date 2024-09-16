package io.codefresh.gradleexample.business.service.bids.impl;

import io.codefresh.gradleexample.business.service.bids.BidHistoryServiceInterface;
import io.codefresh.gradleexample.business.service.bids.BidServiceInterface;
import io.codefresh.gradleexample.business.service.users.UserServiceInterface;
import io.codefresh.gradleexample.business.service.validators.authorization.AuthorizationServiceInterface;
import io.codefresh.gradleexample.business.service.validators.values.ValidationServiceInterface;
import io.codefresh.gradleexample.dao.builders.bid.BidBuilderBase;
import io.codefresh.gradleexample.dao.converters.bids.BidConverter;
import io.codefresh.gradleexample.dao.dto.bids.BidDTO;
import io.codefresh.gradleexample.dao.entities.bids.*;
import io.codefresh.gradleexample.dao.entities.tenders.Tender;
import io.codefresh.gradleexample.dao.repository.bids.BidRepository;
import io.codefresh.gradleexample.dao.repository.bids.DecisionRepository;
import io.codefresh.gradleexample.dao.repository.tenders.TenderRepository;
import io.codefresh.gradleexample.exceptions.service.InvalidEnumException;
import io.codefresh.gradleexample.exceptions.service.bids.BidNotFoundException;
import io.codefresh.gradleexample.exceptions.service.bids.ReviewAlreadySentException;
import io.codefresh.gradleexample.exceptions.service.bids.UserAlreadySentDecisionException;
import io.codefresh.gradleexample.exceptions.service.tenders.TenderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BidServiceImplementation implements BidServiceInterface {

    private static final int QUORUM_SIZE = 3;


    private final BidRepository bidRepository;
    private final TenderRepository tenderRepository;
    private final DecisionRepository decisionRepository;

    private final UserServiceInterface userService;
    private final BidHistoryServiceInterface bidHistoryServiceInterface;

    private final AuthorizationServiceInterface authorizationService;
    private final ValidationServiceInterface validationService;

    private final BidBuilderBase bidBuilder;

    @Autowired
    public BidServiceImplementation(
            BidRepository bidRepository,
            TenderRepository tenderRepository,
            UserServiceInterface userService,
            BidHistoryServiceInterface bidHistoryServiceInterface,
            DecisionRepository decisionRepository,
            AuthorizationServiceInterface authorizationService,
            ValidationServiceInterface validationService,
            BidBuilderBase bidBuilder) {
        this.bidRepository = bidRepository;
        this.tenderRepository = tenderRepository;
        this.userService = userService;
        this.bidHistoryServiceInterface = bidHistoryServiceInterface;
        this.decisionRepository = decisionRepository;
        this.authorizationService = authorizationService;
        this.validationService = validationService;
        this.bidBuilder = bidBuilder;
    }

    @Override
    public BidDTO createBid(String name, String description, String tenderId, String authorType, String authorId) {
        String authorUsername = userService.getEmployeeById(UUID.fromString(authorId)).getUsername();
        if (!validationService.isValidEnumValue(authorType, AuthorType.class)){
            throw new InvalidEnumException("Неверный формат запроса или его параметры.");
        }
        if (AuthorType.valueOf(authorType) == AuthorType.User){
            validateTenderExistence(tenderId, authorUsername);
        } else if (AuthorType.valueOf(authorType) == AuthorType.Organization) {
            validateTenderExistenceAndUserResponses(tenderId, authorUsername);
        }

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
        UUID userID = validationService.checkUserExistAndGetUUIDBack(username);
        List<Bid> entities = bidRepository.findBidsByAuthorId(userID);
        List<BidDTO> sortedEntities = entities.stream()
                .map(BidConverter::toDTO)
                .sorted(Comparator.comparing(BidDTO::getName))
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
        validateTenderExistenceAndUserResponses(tenderId, username);
        UUID userID = validationService.checkUserExistAndGetUUIDBack(username);
        if (bidRepository.findBidsByAuthorId(userID).isEmpty()){
            throw new BidNotFoundException("Тендер или предложение не найдено.");
        }

        Tender tender = tenderRepository.findByCreatorUsername(username).stream().filter(p -> Objects.equals(p.getId(), UUID.fromString(tenderId))).findFirst().orElse(null);
        if (tender == null){
            throw new TenderNotFoundException("Тендер или предложение не найдено.");
        }

        List<Bid> bids = bidRepository.findBidsByTenderId(UUID.fromString(tenderId));
        if (bids.isEmpty()) {
            throw new BidNotFoundException("Тендер или предложение не найдено.");
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
        Bid bid = validationService.checkBidExistsAndIfExistsGetBack(bidId, username);
        authorizationService.checkUserBidResponses(bid.getId(),
                validationService.checkUserExistAndGetUUIDBack(username));
        return bid.getStatus();
    }

    @Override
    public BidDTO updateBidStatus(String bidId, String status, String username) {

        Bid bid = validationService.checkBidExistsAndIfExistsGetBack(bidId, username);
        authorizationService.checkUserBidResponses(bid.getId(),
                validationService.checkUserExistAndGetUUIDBack(username));


        if (!validationService.isValidEnumValue(status, BidsStatuses.class)){
            throw new InvalidEnumException("Неверный формат запроса или его параметры.");
        }

        bid.setStatus(BidsStatuses.valueOf(status));

        bidRepository.save(bid);

        return BidConverter.toDTO(bid);
    }

    @Override
    public BidDTO updateBid(String bidId, String username, Map<String, Object> updates) {
        checkUpdateParameters(updates);
        UUID userId = validationService.checkUserExistAndGetUUIDBack(username);
        Bid bid = validationService.checkBidExistsAndIfExistsGetBack(bidId, username);

        bidHistoryServiceInterface.saveBidHistory(bid);
        authorizationService.checkUserBidResponses(UUID.fromString(bidId), userId);

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
        UUID userId = validationService.checkUUID(bidId);
        validationService.checkUserExistAndGetUUIDBack(username);
        validationService.checkBidExistsAndIfExistsGetBack(bidId, username);
        authorizationService.checkUserBidResponses(UUID.fromString(bidId), userId);
        return bidHistoryServiceInterface.rollbackBid(UUID.fromString(bidId), version, username);
    }


    @Override
    public void submitDecision(String bidId, String decision, String username) {
        if(!validationService.isValidEnumValue(decision, BidDecision.class)){
            throw new InvalidEnumException("Решение не может быть отправлено.");
        }

        Bid bid = validationService.checkBidExistsAndIfExistsGetBack(bidId, username);
        validationService.checkUserExistAndGetUUIDBack(username);

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

        long approvedCount = existingDecisions.stream()
                .filter(d -> d.getDecision() == BidDecision.Approved)
                .count();

        if (approvedCount + (BidDecision.valueOf(decision) == BidDecision.Approved ? 1 : 0) >= QUORUM_SIZE) {
            bid.setDecisionStatus(BidDecision.Approved);
        } else {
            bid.setDecisionStatus(BidDecision.Quorum);
        }

        bidRepository.save(bid);
    }

    @Override
    public void submitFeedback(String bidId, String feedback, String username) {
        validationService.checkUUID((bidId));
        validationService.checkUserExistAndGetUUIDBack(username);

        Bid bid = validationService.checkBidExistsAndIfExistsGetBack(bidId, username);
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

    private void validateTenderExistenceAndUserResponses(String tenderID, String username) {
        Tender tender = validationService.checkTenderExistsAndIfExistsGetBack(tenderID);
        UUID userId = validationService.checkUserExistAndGetUUIDBack(username);
        authorizationService.checkUserOrganizationResponses(tender.getOrganization_id(), userId);
    }

    private void validateTenderExistence(String tenderID, String username){
        Tender tender = validationService.checkTenderExistsAndIfExistsGetBack(tenderID);
        UUID userId = validationService.checkUserExistAndGetUUIDBack(username);
    }

    private void checkUpdateParameters(Map<String, Object> updates){
        if (updates.containsKey("authorType")){
            if (!validationService.isValidEnumValue(String.valueOf(updates.get("authorType")), AuthorType.class)){
                throw new InvalidEnumException("Данные неправильно сформированы или не соответствуют требованиям.");
            }
        }

        if (updates.containsKey("bidDecision")){
            if (!validationService.isValidEnumValue(String.valueOf(updates.get("bidDecision")), BidDecision.class)){
                throw new InvalidEnumException("Данные неправильно сформированы или не соответствуют требованиям.");
            }
        }
        if (updates.containsKey("status")){
            if (!validationService.isValidEnumValue(String.valueOf(updates.get("status")), BidsStatuses.class)){
                throw new InvalidEnumException("Данные неправильно сформированы или не соответствуют требованиям.");
            }
        }
        if (updates.containsKey("bidId")){
            validationService.checkUUID(updates.containsKey("bidId") ? String.valueOf(updates.get("bidId")) : null);
        }
        if (updates.containsKey("authorId")){
            validationService.checkUUID((updates.containsKey("authorId") ? String.valueOf(updates.get("authorId")) : null));
        }
        if (updates.containsKey("tenderId")){
            validationService.checkUUID((updates.containsKey("tenderId") ? String.valueOf(updates.get("tenderId")) : null));
        }
        if (updates.containsKey("reviewId")){
            validationService.checkUUID((updates.containsKey("reviewId") ? String.valueOf(updates.get("reviewId")) : null));
        }
    }
}
