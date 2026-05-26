package com.opsboard.incident.dto;

import com.opsboard.incident.data.IncidentSeverity;
import com.opsboard.incident.data.IncidentStatus;
import java.time.Instant;
import java.util.UUID;

public record IncidentResponse(
        UUID id,
        String title,
        String description,
        IncidentSeverity severity,
        IncidentStatus status,
        UUID teamId,
        Instant createdAt,
        Instant updatedAt
) {
}

