package io.codefresh.gradleexample.web.controllers;


import io.codefresh.gradleexample.business.service.bids.BidServiceInterface;
import io.codefresh.gradleexample.business.service.review.BidReviewServiceInterface;
import io.codefresh.gradleexample.dao.dto.bids.BidCreationDTO;
import io.codefresh.gradleexample.dao.dto.bids.BidDTO;
import io.codefresh.gradleexample.dao.dto.bids.BidReviewDTO;
import io.codefresh.gradleexample.dao.entities.bids.BidReview;
import io.codefresh.gradleexample.dao.entities.bids.BidsStatuses;
import io.codefresh.gradleexample.exceptions.ErrorResponse;
import io.codefresh.gradleexample.exceptions.service.InvalidEnumException;
import io.codefresh.gradleexample.exceptions.service.InvalidUUIDException;
import io.codefresh.gradleexample.exceptions.service.bids.BidNotFoundException;
import io.codefresh.gradleexample.exceptions.service.bids.UserAlreadySentDecisionException;
import io.codefresh.gradleexample.exceptions.service.employee.EmployeeHasNoResponsibleException;
import io.codefresh.gradleexample.exceptions.service.employee.EmployeeNotFoundException;
import io.codefresh.gradleexample.exceptions.service.tenders.TenderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bids")
public class BidsController {
    private final BidServiceInterface bidService;
    private final BidReviewServiceInterface reviewService;

    @Autowired
    public BidsController(BidServiceInterface bidService,
                          BidReviewServiceInterface reviewService) {
        this.bidService = bidService;
        this.reviewService = reviewService;
    }

    @PostMapping("/new")
    public ResponseEntity<?> createBid(@RequestBody BidCreationDTO bid) {
        try{
            BidDTO created = bidService.createBid(
                    bid.getName(),
                    bid.getDescription(),
                    bid.getTenderId(),
                    bid.getAuthorType(),
                    bid.getAuthorId());
            return ResponseEntity.ok(created);
        }catch (TenderNotFoundException | EmployeeNotFoundException e){
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.NOT_FOUND);
        }catch (InvalidEnumException | InvalidUUIDException e){
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.BAD_REQUEST);
        }catch (EmployeeHasNoResponsibleException e){
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.FORBIDDEN);
        }catch (Exception e){
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyBids(
            @RequestParam(name = "limit", defaultValue = "5", required = false) int limit,
            @RequestParam(name = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(name = "username") String username) {
        try {
            List<BidDTO> result = bidService.getBidsByUsername(limit, offset, username);
            return ResponseEntity.ok(result);
        }catch (EmployeeNotFoundException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{tenderId}/list")
    public ResponseEntity<?> getTenderBids(
            @PathVariable String tenderId,
            @RequestParam(name = "username") String username,
            @RequestParam(name = "limit", defaultValue = "5", required = false) Integer limit,
            @RequestParam(name = "offset", defaultValue = "0", required = false) Integer offset
            ) {
        try {
            List<BidDTO> bids = bidService.getTenderBids(limit, offset, username, tenderId);
            return ResponseEntity.ok(bids);
        }catch (TenderNotFoundException | EntityNotFoundException | EmployeeNotFoundException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.NOT_FOUND);
        }catch (EmployeeHasNoResponsibleException e){
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/{bidId}/status")
    public ResponseEntity<?> getBidStatus(
            @PathVariable String bidId,
            @RequestParam(name = "username") String username) {
        try{
            BidsStatuses bidsStatuses = bidService.getBidsStatuses(bidId, username);
            return ResponseEntity.ok(bidsStatuses);
        } catch (BidNotFoundException | EmployeeNotFoundException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.NOT_FOUND);
        } catch (EmployeeHasNoResponsibleException e){
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/{bidId}/status")
    public ResponseEntity<?> updateBidStatus(
            @PathVariable String bidId,
            @RequestParam String status,
            @RequestParam String username){
        try{
            BidDTO bid = bidService.updateBidStatus(bidId, status, username);
            return ResponseEntity.ok(bid);
        }catch (IllegalArgumentException | InvalidEnumException e){
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.BAD_REQUEST);
        }catch (BidNotFoundException | EmployeeNotFoundException e){
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.NOT_FOUND);
        }catch (EmployeeHasNoResponsibleException e){
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.FORBIDDEN);
        }
    }

    @PatchMapping("/{bidId}/edit")
    public ResponseEntity<?> editTender(
            @PathVariable String bidId,
            @RequestParam(name = "username") String username,
            @RequestBody Map<String, Object> updates
    ) {
        try {
            BidDTO updatedBid = bidService.updateBid(bidId, username, updates);
            return ResponseEntity.ok(updatedBid);
        } catch (BidNotFoundException | EmployeeNotFoundException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.NOT_FOUND);
        } catch (EmployeeHasNoResponsibleException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.FORBIDDEN);
        } catch (InvalidEnumException | InvalidUUIDException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            ErrorResponse error = new ErrorResponse("Ошибка при обновлении предложения.");
            return error.toResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{bidId}/rollback/{version}")
    public ResponseEntity<?> rollbackBid(
            @PathVariable String bidId,
            @PathVariable int version,
            @RequestParam String username
    ){
        try {
            BidDTO updatedBid = bidService.rollbackBid(bidId, username, version);
            return ResponseEntity.ok(updatedBid);
        } catch (EntityNotFoundException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.NOT_FOUND);
        } catch (EmployeeHasNoResponsibleException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.FORBIDDEN);
        } catch (InvalidEnumException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{bidId}/submit_decision")
    public ResponseEntity<?> submitDecision(
            @PathVariable String bidId,
            @RequestParam String decision,
            @RequestParam String username) {

        try {
            bidService.submitDecision(bidId, decision, username);
            return ResponseEntity.ok("Предложение успешно отправлено.");
        }catch (EmployeeNotFoundException | BidNotFoundException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.NOT_FOUND);
        } catch (UserAlreadySentDecisionException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/{bidId}/feedback")
    public ResponseEntity<?> submitFeedback(
            @PathVariable String bidId,
            @RequestParam String bidFeedback,
            @RequestParam String username) {

        try {
            bidService.submitFeedback(bidId, bidFeedback, username);
            return ResponseEntity.ok("Отзыв успешно отправлен.");
        } catch (EmployeeNotFoundException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.UNAUTHORIZED);
        } catch (BidNotFoundException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{tenderId}/reviews")
    public ResponseEntity<?> getBidReviews(
            @PathVariable String tenderId,
            @RequestParam String authorUsername,
            @RequestParam String requesterUsername,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        try {
            List<BidReview> reviews = reviewService.getBidReviews(tenderId, authorUsername, requesterUsername, limit, offset);
            List<BidReviewDTO> reviewDTOs = reviews.stream()
                    .map(BidReviewDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(reviewDTOs);
        } catch (InvalidUUIDException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (EmployeeNotFoundException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.UNAUTHORIZED);
        } catch (EmployeeHasNoResponsibleException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.FORBIDDEN);
        } catch (BidNotFoundException | TenderNotFoundException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
