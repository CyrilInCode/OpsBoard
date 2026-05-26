package com.opsboard.team.controller;

import com.opsboard.team.dto.CreateTeamRequest;
import com.opsboard.team.dto.TeamResponse;
import com.opsboard.team.dto.UpdateOnCallRequest;
import com.opsboard.team.service.TeamService;
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
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService service;

    public TeamController(TeamService service) {
        this.service = service;
    }

    @GetMapping
    public List<TeamResponse> findAll(@RequestParam(name = "serviceName") Optional<String> serviceName) {
        return service.findAll(serviceName);
    }

    @GetMapping("/{id}")
    public TeamResponse getById(@PathVariable("id") UUID id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<TeamResponse> create(@Valid @RequestBody CreateTeamRequest request) {
        var created = service.create(request);
        return ResponseEntity.created(URI.create("/api/teams/" + created.id())).body(created);
    }

    @PatchMapping("/{id}/on-call")
    public TeamResponse updateOnCall(@PathVariable("id") UUID id, @Valid @RequestBody UpdateOnCallRequest request) {
        return service.updateOnCall(id, request);
    }
}
