package com.opsboard.incident.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class IncidentRepositoryTest {

    private static final UUID TEAM_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private IncidentRepository repository;

    @DynamicPropertySource
    static void registerDatabaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Test
    void findsIncidentsByStatus() {
        var incident = repository.save(new Incident("CPU alert", "CPU is above threshold", IncidentSeverity.HIGH,
                IncidentStatus.OPEN, TEAM_ID));

        var openIncidents = repository.findByStatusOrderByCreatedAtDesc(IncidentStatus.OPEN);

        assertThat(openIncidents)
                .extracting(Incident::getId)
                .contains(incident.getId());
    }

    @Test
    void findsIncidentsByTeam() {
        var otherTeam = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        repository.save(new Incident("Team A issue", "Owned by team A", IncidentSeverity.LOW, IncidentStatus.OPEN, TEAM_ID));
        repository.save(new Incident("Team B issue", "Owned by team B", IncidentSeverity.LOW, IncidentStatus.OPEN, otherTeam));

        var teamIncidents = repository.findByTeamIdOrderByCreatedAtDesc(TEAM_ID);

        assertThat(teamIncidents)
                .allMatch(incident -> incident.getTeamId().equals(TEAM_ID));
    }
}

