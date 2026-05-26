package com.opsboard.team.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTeamRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 120) String serviceName,
        @NotBlank @Email @Size(max = 160) String contactEmail,
        @NotBlank @Size(max = 120) String onCallEngineer,
        @Min(1) @Max(20) int capacity
) {
}

