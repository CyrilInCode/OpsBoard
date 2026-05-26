package com.opsboard.team.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.opsboard.team.data.Team;
import com.opsboard.team.data.TeamRepository;
import com.opsboard.team.dto.CreateTeamRequest;
import com.opsboard.team.dto.UpdateOnCallRequest;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository repository;

    @InjectMocks
    private TeamService service;

    @Test
    void createsTeamWhenNameIsUnique() {
        when(repository.existsByNameIgnoreCase("Platform Team")).thenReturn(false);
        when(repository.save(any(Team.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.create(new CreateTeamRequest(" Platform Team ", " gateway ", "platform@example.com", " Nadia ", 8));

        var captor = ArgumentCaptor.forClass(Team.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Platform Team");
        assertThat(captor.getValue().getServiceName()).isEqualTo("gateway");
    }

    @Test
    void rejectsDuplicateTeamName() {
        when(repository.existsByNameIgnoreCase("Platform Team")).thenReturn(true);

        assertThatThrownBy(() -> service.create(new CreateTeamRequest(
                "Platform Team",
                "gateway",
                "platform@example.com",
                "Nadia",
                8
        ))).isInstanceOf(DuplicateTeamException.class);
    }

    @Test
    void updatesOnCallEngineer() {
        var id = UUID.randomUUID();
        var team = new Team("Platform Team", "gateway", "platform@example.com", "Nadia", 8);
        when(repository.findById(id)).thenReturn(Optional.of(team));

        var response = service.updateOnCall(id, new UpdateOnCallRequest(" Leo "));

        assertThat(response.onCallEngineer()).isEqualTo("Leo");
    }

    @Test
    void throwsWhenTeamDoesNotExist() {
        var id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(id))
                .isInstanceOf(TeamNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}

