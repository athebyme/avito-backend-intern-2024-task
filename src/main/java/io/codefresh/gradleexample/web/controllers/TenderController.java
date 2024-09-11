package io.codefresh.gradleexample.web.controllers;

import io.codefresh.gradleexample.business.service.TenderServiceInterface;
import io.codefresh.gradleexample.dao.dto.TenderDTO;
import io.codefresh.gradleexample.dao.entities.tenders.ServiceTypes;
import io.codefresh.gradleexample.dao.entities.tenders.TenderStatuses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tenders")
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
            @RequestParam(name = "service_type", required = false) ServiceTypes type
    ) {
        return tenderService.getAllTenders(limit, offset, type);
    }

    @GetMapping("/my")
    public List<TenderDTO> getMyTenders(
            @RequestParam(name = "limit", required = false) Integer limit,
            @RequestParam(name = "offset", required = false) Integer offset,
            @RequestParam(name = "username", required = false) String username
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
}
