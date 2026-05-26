package com.opsboard.team.service;

import java.util.UUID;

public class TeamNotFoundException extends RuntimeException {

    public TeamNotFoundException(UUID id) {
        super("Team not found: " + id);
    }
}

