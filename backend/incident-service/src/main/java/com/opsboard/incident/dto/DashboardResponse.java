package com.opsboard.incident.dto;

public record DashboardResponse(
        long total,
        long open,
        long inProgress,
        long resolved,
        long critical
) {
}

