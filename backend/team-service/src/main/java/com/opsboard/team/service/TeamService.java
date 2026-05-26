package com.opsboard.team.service;

import com.opsboard.team.data.Team;
import com.opsboard.team.data.TeamRepository;
import com.opsboard.team.dto.CreateTeamRequest;
import com.opsboard.team.dto.TeamResponse;
import com.opsboard.team.dto.UpdateOnCallRequest;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository repository;

    public TeamService(TeamRepository repository) {
        this.repository = repository;
    }

    public List<TeamResponse> findAll(Optional<String> serviceName) {
        var teams = serviceName
                .filter(value -> !value.isBlank())
                .map(repository::findByServiceNameContainingIgnoreCaseOrderByNameAsc)
                .orElseGet(repository::findAll);

        return teams.stream()
                .sorted(Comparator.comparing(Team::getName))
                .map(TeamMapper::toResponse)
                .toList();
    }

    public TeamResponse getById(UUID id) {
        return repository.findById(id)
                .map(TeamMapper::toResponse)
                .orElseThrow(() -> new TeamNotFoundException(id));
    }

    @Transactional
    public TeamResponse create(CreateTeamRequest request) {
        var name = request.name().trim();
        if (repository.existsByNameIgnoreCase(name)) {
            throw new DuplicateTeamException(name);
        }

        var team = new Team(
                name,
                request.serviceName().trim(),
                request.contactEmail().trim(),
                request.onCallEngineer().trim(),
                request.capacity()
        );

        return TeamMapper.toResponse(repository.save(team));
    }

    @Transactional
    public TeamResponse updateOnCall(UUID id, UpdateOnCallRequest request) {
        var team = repository.findById(id)
                .orElseThrow(() -> new TeamNotFoundException(id));
        team.updateOnCallEngineer(request.onCallEngineer().trim());
        return TeamMapper.toResponse(team);
    }
}

