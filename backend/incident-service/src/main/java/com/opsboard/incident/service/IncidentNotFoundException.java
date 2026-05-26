package com.opsboard.incident.service;

import java.util.UUID;

public class IncidentNotFoundException extends RuntimeException {

    public IncidentNotFoundException(UUID id) {
        super("Incident not found: " + id);
    }
}

