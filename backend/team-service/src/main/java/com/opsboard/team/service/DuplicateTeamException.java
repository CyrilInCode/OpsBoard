package com.opsboard.team.service;

public class DuplicateTeamException extends RuntimeException {

    public DuplicateTeamException(String name) {
        super("Team already exists: " + name);
    }
}

