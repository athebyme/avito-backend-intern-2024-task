package io.codefresh.gradleexample.web.controllers;

import io.codefresh.gradleexample.business.service.tenders.TenderServiceInterface;
import io.codefresh.gradleexample.dao.dto.tenders.TenderCreationRequest;
import io.codefresh.gradleexample.dao.dto.tenders.TenderDTO;
import io.codefresh.gradleexample.dao.entities.tenders.TenderStatuses;
import io.codefresh.gradleexample.exceptions.ErrorResponse;
import io.codefresh.gradleexample.exceptions.service.InvalidUUIDException;
import io.codefresh.gradleexample.exceptions.service.employee.EmployeeHasNoResponsibleException;
import io.codefresh.gradleexample.exceptions.service.employee.EmployeeNotFoundException;
import io.codefresh.gradleexample.exceptions.service.InvalidEnumException;
import io.codefresh.gradleexample.exceptions.service.tenders.TenderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tenders")
public class TenderController {
    private final TenderServiceInterface tenderService;

    @Autowired
    public TenderController(TenderServiceInterface tenderService) {
        this.tenderService = tenderService;
    }

    @GetMapping
    public List<TenderDTO> getTenders(
            @RequestParam(name = "limit", required = false, defaultValue = "5") int limit,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "service_type", required = false) List<String> serviceTypes
    ) {
        return tenderService.getAllTenders(limit, offset, serviceTypes);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyTenders(
            @RequestParam(name = "limit", required = false, defaultValue = "5") int limit,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "username") String username
    ) {
        try{
            List<TenderDTO> tenderDTOS = tenderService.getTendersByUsername(limit, offset, username);
            return ResponseEntity.ok(tenderDTOS);
        }catch (EmployeeNotFoundException e){
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{tenderID}/status")
    public ResponseEntity<?> getTenderStatus(
            @PathVariable String tenderID,
            @RequestParam(name = "username", required = false) String username
    ) {
        try{
            TenderStatuses status = tenderService.getTenderStatus(tenderID, username);
            return ResponseEntity.ok(status);
        }catch (EmployeeNotFoundException | TenderNotFoundException e){
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.NOT_FOUND);
        } catch (InvalidEnumException | InvalidUUIDException e) {
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

    @PostMapping("/new")
    public ResponseEntity<?> createTender(
            @RequestBody() TenderCreationRequest tenderDTO
    ){
        try{
            TenderDTO createdTender = tenderService.createTender(
                    tenderDTO.getName(),
                    tenderDTO.getDescription(),
                    tenderDTO.getServiceType(),
                    tenderDTO.getOrganizationId(),
                    tenderDTO.getCreatorUsername());
            return ResponseEntity.ok(createdTender);
        }catch (EmployeeNotFoundException e){
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.NOT_FOUND);
        }catch (EmployeeHasNoResponsibleException e){
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.FORBIDDEN);
        } catch (InvalidEnumException | InvalidUUIDException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{tenderID}/status")
    public ResponseEntity<?> updateTenderStatus(
            @PathVariable String tenderID,
            @RequestParam(name = "status") String status,
            @RequestParam(name = "username") String username
    ){
        try{
            TenderDTO updatedTender = tenderService.changeTenderStatus(tenderID, status, username);
            return ResponseEntity.ok(updatedTender);
        } catch (EmployeeNotFoundException | TenderNotFoundException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.NOT_FOUND);
        } catch (InvalidEnumException | InvalidUUIDException e) {
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

    @PatchMapping("/{tenderId}/edit")
    public ResponseEntity<?> editTender(
            @PathVariable String tenderId,
            @RequestParam(name = "username") String username,
            @RequestBody Map<String, Object> updates
    ) {
        try {
            TenderDTO updatedTender = tenderService.editTender(tenderId, username, updates);
            return ResponseEntity.ok(updatedTender);
        } catch (TenderNotFoundException | EmployeeNotFoundException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.NOT_FOUND);
        } catch (EmployeeHasNoResponsibleException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.FORBIDDEN);
        } catch (InvalidEnumException | InvalidUUIDException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{tenderId}/rollback/{version}")
    public ResponseEntity<?> rollbackTenderVersion(
            @PathVariable String tenderId,
            @PathVariable int version,
            @RequestParam String username) {

        try {
            TenderDTO updatedTender = tenderService.rollbackTender(tenderId, version, username);
            return ResponseEntity.ok(updatedTender);
        } catch (EntityNotFoundException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.NOT_FOUND);
        } catch (EmployeeHasNoResponsibleException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.FORBIDDEN);
        } catch (InvalidEnumException | InvalidUUIDException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error.toResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
