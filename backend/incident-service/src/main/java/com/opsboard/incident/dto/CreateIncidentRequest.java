package com.opsboard.incident.dto;

import com.opsboard.incident.data.IncidentSeverity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateIncidentRequest(
        @NotBlank @Size(max = 120) String title,
        @NotBlank @Size(max = 2000) String description,
        @NotNull IncidentSeverity severity,
        @NotNull UUID teamId
) {
}

