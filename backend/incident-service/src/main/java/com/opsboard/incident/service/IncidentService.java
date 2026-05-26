package com.opsboard.incident.service;

import com.opsboard.incident.data.Incident;
import com.opsboard.incident.data.IncidentRepository;
import com.opsboard.incident.data.IncidentSeverity;
import com.opsboard.incident.data.IncidentStatus;
import com.opsboard.incident.dto.CreateIncidentRequest;
import com.opsboard.incident.dto.DashboardResponse;
import com.opsboard.incident.dto.IncidentResponse;
import com.opsboard.incident.dto.UpdateIncidentStatusRequest;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class IncidentService {

    private final IncidentRepository repository;

    public IncidentService(IncidentRepository repository) {
        this.repository = repository;
    }

    public List<IncidentResponse> findAll(Optional<IncidentStatus> status) {
        var incidents = status
                .map(repository::findByStatusOrderByCreatedAtDesc)
                .orElseGet(repository::findAll);

        return incidents.stream()
                .sorted(Comparator.comparing(Incident::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(IncidentMapper::toResponse)
                .toList();
    }

    public List<IncidentResponse> findByTeam(UUID teamId) {
        return repository.findByTeamIdOrderByCreatedAtDesc(teamId).stream()
                .map(IncidentMapper::toResponse)
                .toList();
    }

    public IncidentResponse getById(UUID id) {
        return repository.findById(id)
                .map(IncidentMapper::toResponse)
                .orElseThrow(() -> new IncidentNotFoundException(id));
    }

    @Transactional
    public IncidentResponse create(CreateIncidentRequest request) {
        var incident = new Incident(
                request.title().trim(),
                request.description().trim(),
                request.severity(),
                IncidentStatus.OPEN,
                request.teamId()
        );

        return IncidentMapper.toResponse(repository.save(incident));
    }

    @Transactional
    public IncidentResponse updateStatus(UUID id, UpdateIncidentStatusRequest request) {
        var incident = repository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(id));
        incident.updateStatus(request.status());
        return IncidentMapper.toResponse(incident);
    }

    public DashboardResponse dashboard() {
        var incidents = repository.findAll();
        return new DashboardResponse(
                incidents.size(),
                countStatus(incidents, IncidentStatus.OPEN),
                countStatus(incidents, IncidentStatus.IN_PROGRESS),
                countStatus(incidents, IncidentStatus.RESOLVED),
                incidents.stream().filter(incident -> incident.getSeverity() == IncidentSeverity.CRITICAL).count()
        );
    }

    private static long countStatus(List<Incident> incidents, IncidentStatus status) {
        return incidents.stream()
                .filter(incident -> incident.getStatus() == status)
                .count();
    }
}

