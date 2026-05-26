package com.opsboard.incident.dto;

import com.opsboard.incident.data.IncidentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateIncidentStatusRequest(@NotNull IncidentStatus status) {
}

