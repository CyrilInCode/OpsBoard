package com.opsboard.incident.service;

import com.opsboard.incident.data.Incident;
import com.opsboard.incident.dto.IncidentResponse;

final class IncidentMapper {

    private IncidentMapper() {
    }

    static IncidentResponse toResponse(Incident incident) {
        return new IncidentResponse(
                incident.getId(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getSeverity(),
                incident.getStatus(),
                incident.getTeamId(),
                incident.getCreatedAt(),
                incident.getUpdatedAt()
        );
    }
}

