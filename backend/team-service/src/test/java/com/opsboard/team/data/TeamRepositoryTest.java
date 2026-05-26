package com.opsboard.team.data;

import static org.assertj.core.api.Assertions.assertThat;

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
class TeamRepositoryTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private TeamRepository repository;

    @DynamicPropertySource
    static void registerDatabaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Test
    void findsTeamByServiceName() {
        repository.save(new Team("Edge Team", "gateway", "edge@example.com", "Nadia", 8));
        repository.save(new Team("Compliance Team", "supply-chain", "compliance@example.com", "Ines", 4));

        var teams = repository.findByServiceNameContainingIgnoreCaseOrderByNameAsc("gate");

        assertThat(teams)
                .extracting(Team::getName)
                .containsExactly("Edge Team", "Platform Team");
    }

    @Test
    void detectsExistingTeamNameCaseInsensitive() {
        repository.save(new Team("Analytics Team", "warehouse", "analytics@example.com", "Leo", 5));

        assertThat(repository.existsByNameIgnoreCase("analytics team")).isTrue();
    }
}
