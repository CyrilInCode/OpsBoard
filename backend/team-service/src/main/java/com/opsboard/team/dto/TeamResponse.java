package com.opsboard.team.dto;

import java.time.Instant;
import java.util.UUID;

public record TeamResponse(
        UUID id,
        String name,
        String serviceName,
        String contactEmail,
        String onCallEngineer,
        int capacity,
        Instant createdAt,
        Instant updatedAt
) {
}

