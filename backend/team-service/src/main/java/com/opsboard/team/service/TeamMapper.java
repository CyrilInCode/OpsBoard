package com.opsboard.team.service;

import com.opsboard.team.data.Team;
import com.opsboard.team.dto.TeamResponse;

final class TeamMapper {

    private TeamMapper() {
    }

    static TeamResponse toResponse(Team team) {
        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.getServiceName(),
                team.getContactEmail(),
                team.getOnCallEngineer(),
                team.getCapacity(),
                team.getCreatedAt(),
                team.getUpdatedAt()
        );
    }
}

