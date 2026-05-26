package com.opsboard.incident.controller;

import com.opsboard.incident.data.IncidentStatus;
import com.opsboard.incident.dto.CreateIncidentRequest;
import com.opsboard.incident.dto.DashboardResponse;
import com.opsboard.incident.dto.IncidentResponse;
import com.opsboard.incident.dto.UpdateIncidentStatusRequest;
import com.opsboard.incident.service.IncidentService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService service;

    public IncidentController(IncidentService service) {
        this.service = service;
    }

    @GetMapping
    public List<IncidentResponse> findAll(@RequestParam(name = "status") Optional<IncidentStatus> status,
            @RequestParam(name = "teamId") Optional<UUID> teamId) {
        return teamId.map(service::findByTeam).orElseGet(() -> service.findAll(status));
    }

    @GetMapping("/{id}")
    public IncidentResponse getById(@PathVariable("id") UUID id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<IncidentResponse> create(@Valid @RequestBody CreateIncidentRequest request) {
        var created = service.create(request);
        return ResponseEntity.created(URI.create("/api/incidents/" + created.id())).body(created);
    }

    @PatchMapping("/{id}/status")
    public IncidentResponse updateStatus(@PathVariable("id") UUID id, @Valid @RequestBody UpdateIncidentStatusRequest request) {
        return service.updateStatus(id, request);
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return service.dashboard();
    }
}
