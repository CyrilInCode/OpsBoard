package com.opsboard.team.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateOnCallRequest(@NotBlank @Size(max = 120) String onCallEngineer) {
}

