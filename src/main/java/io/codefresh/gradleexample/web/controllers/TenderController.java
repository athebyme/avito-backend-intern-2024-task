package io.codefresh.gradleexample.web.controllers;

import io.codefresh.gradleexample.business.service.TenderServiceInterface;
import io.codefresh.gradleexample.dao.dto.tenders.TenderCreationResponse;
import io.codefresh.gradleexample.dao.dto.tenders.TenderDTO;
import io.codefresh.gradleexample.dao.entities.tenders.TenderStatuses;
import io.codefresh.gradleexample.exceptions.service.EmployeeHasNoResponsibleException;
import io.codefresh.gradleexample.exceptions.service.EmployeeNotFoundException;
import io.codefresh.gradleexample.exceptions.service.InvalidEnumException;
import io.codefresh.gradleexample.exceptions.service.TenderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
            @RequestParam(name = "limit", required = false) Integer limit,
            @RequestParam(name = "offset", required = false) Integer offset,
            @RequestParam(name = "service_type", required = false) List<String> serviceTypes
    ) {
        return tenderService.getAllTenders(limit, offset, serviceTypes);
    }

    @GetMapping("/my")
    public List<TenderDTO> getMyTenders(
            @RequestParam(name = "limit", required = false) Integer limit,
            @RequestParam(name = "offset", required = false) Integer offset,
            @RequestParam(name = "username") String username
    ) {
        return tenderService.getTendersByUsername(limit, offset, username);
    }

    @GetMapping("/{tenderID}/status")
    public TenderStatuses getTenderStatus(
            @PathVariable UUID tenderID,
            @RequestParam(name = "username", required = false) String username
    ) {
        return tenderService.tenderStatuses(tenderID, username);
    }

    @PostMapping("/new")
    public TenderDTO createTender(
            @RequestBody() TenderCreationResponse tenderDTO
    ){
        return tenderService.createTender(
                tenderDTO.getName(),
                tenderDTO.getDescription(),
                tenderDTO.getServiceType(),
                tenderDTO.getOrganizationId(),
                tenderDTO.getCreatorUsername());
    }

    @PutMapping("/{tenderID}/status")
    public TenderDTO updateTenderStatus(
            @PathVariable UUID tenderID,
            @RequestParam(name = "status") String status,
            @RequestParam(name = "username") String username
    ){
        return tenderService.changeTenderStatus(tenderID, status, username);
    }

    @PatchMapping("/{tenderId}/edit")
    public ResponseEntity<?> editTender(
            @PathVariable UUID tenderId,
            @RequestParam(name = "username") String username,
            @RequestBody Map<String, Object> updates
    ) {
        try {
            TenderDTO updatedTender = tenderService.editTender(tenderId, username, updates);
            return ResponseEntity.ok(updatedTender);
        } catch (TenderNotFoundException ex) {
            return errorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (EmployeeNotFoundException ex) {
            return errorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (EmployeeHasNoResponsibleException ex) {
            return errorResponse(ex.getMessage(), HttpStatus.FORBIDDEN);
        } catch (InvalidEnumException ex) {
          return errorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return errorResponse("Ошибка при обновлении тендера", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{tenderId}/rollback/{version}")
    public ResponseEntity<?> rollbackTenderVersion(
            @PathVariable UUID tenderId,
            @PathVariable int version,
            @RequestParam String username) {

        try {
            TenderDTO updatedTender = tenderService.rollbackTender(tenderId, version, username);
            return ResponseEntity.ok(updatedTender);
        } catch (EntityNotFoundException e) {
            return errorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (EmployeeHasNoResponsibleException e) {
            return errorResponse(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (InvalidEnumException e) {
            return errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }



    @ExceptionHandler(TenderNotFoundException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleTenderNotFoundException(TenderNotFoundException ex) {
        return errorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleEmployeeNotFoundException(EmployeeNotFoundException ex) {
        return errorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidEnumException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleInvalidEnumException(InvalidEnumException ex) {
        return errorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmployeeHasNoResponsibleException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleEmployeeHasNoResponsible(EmployeeHasNoResponsibleException ex) {
        return errorResponse(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<Map<String, String>> errorResponse(String message, HttpStatus status) {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("reason", message);
        return new ResponseEntity<>(responseBody, status);
    }
}
