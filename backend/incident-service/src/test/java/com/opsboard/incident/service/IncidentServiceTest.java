package com.opsboard.incident.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.opsboard.incident.data.Incident;
import com.opsboard.incident.data.IncidentRepository;
import com.opsboard.incident.data.IncidentSeverity;
import com.opsboard.incident.data.IncidentStatus;
import com.opsboard.incident.dto.CreateIncidentRequest;
import com.opsboard.incident.dto.UpdateIncidentStatusRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    private static final UUID TEAM_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    @Mock
    private IncidentRepository repository;

    @InjectMocks
    private IncidentService service;

    @Test
    void createsIncidentOpenByDefault() {
        when(repository.save(any(Incident.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.create(new CreateIncidentRequest(
                " Gateway down ",
                " Public gateway returns 503 ",
                IncidentSeverity.CRITICAL,
                TEAM_ID
        ));

        var captor = ArgumentCaptor.forClass(Incident.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("Gateway down");
        assertThat(response.status()).isEqualTo(IncidentStatus.OPEN);
    }

    @Test
    void updatesIncidentStatus() {
        var id = UUID.randomUUID();
        var incident = new Incident("Backup warning", "Backup duration increased", IncidentSeverity.MEDIUM,
                IncidentStatus.OPEN, TEAM_ID);
        when(repository.findById(id)).thenReturn(Optional.of(incident));

        var response = service.updateStatus(id, new UpdateIncidentStatusRequest(IncidentStatus.RESOLVED));

        assertThat(response.status()).isEqualTo(IncidentStatus.RESOLVED);
    }

    @Test
    void throwsWhenIncidentDoesNotExist() {
        var id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(id))
                .isInstanceOf(IncidentNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void buildsDashboardCounts() {
        when(repository.findAll()).thenReturn(List.of(
                new Incident("A", "A desc", IncidentSeverity.CRITICAL, IncidentStatus.OPEN, TEAM_ID),
                new Incident("B", "B desc", IncidentSeverity.HIGH, IncidentStatus.IN_PROGRESS, TEAM_ID),
                new Incident("C", "C desc", IncidentSeverity.LOW, IncidentStatus.RESOLVED, TEAM_ID)
        ));

        var dashboard = service.dashboard();

        assertThat(dashboard.total()).isEqualTo(3);
        assertThat(dashboard.open()).isEqualTo(1);
        assertThat(dashboard.inProgress()).isEqualTo(1);
        assertThat(dashboard.resolved()).isEqualTo(1);
        assertThat(dashboard.critical()).isEqualTo(1);
    }
}

