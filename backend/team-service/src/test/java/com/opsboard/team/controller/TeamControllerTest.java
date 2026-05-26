package com.opsboard.team.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opsboard.team.dto.CreateTeamRequest;
import com.opsboard.team.dto.TeamResponse;
import com.opsboard.team.dto.UpdateOnCallRequest;
import com.opsboard.team.service.DuplicateTeamException;
import com.opsboard.team.service.TeamNotFoundException;
import com.opsboard.team.service.TeamService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TeamController.class)
class TeamControllerTest {

    private static final UUID TEAM_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeamService service;

    @Test
    void listsTeams() throws Exception {
        when(service.findAll(any())).thenReturn(List.of(response("Nadia Martin")));

        mockMvc.perform(get("/api/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Platform Team"))
                .andExpect(jsonPath("$[0].serviceName").value("gateway"));
    }

    @Test
    void createsTeam() throws Exception {
        when(service.create(any(CreateTeamRequest.class))).thenReturn(response("Nadia Martin"));

        var request = new CreateTeamRequest("Platform Team", "gateway", "platform@example.com", "Nadia Martin", 8);

        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/teams/" + TEAM_ID))
                .andExpect(jsonPath("$.capacity").value(8));
    }

    @Test
    void updatesOnCallEngineer() throws Exception {
        when(service.updateOnCall(eq(TEAM_ID), any(UpdateOnCallRequest.class))).thenReturn(response("Leo Bernard"));

        mockMvc.perform(patch("/api/teams/{id}/on-call", TEAM_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateOnCallRequest("Leo Bernard"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.onCallEngineer").value("Leo Bernard"));
    }

    @Test
    void returnsNotFoundProblem() throws Exception {
        when(service.getById(TEAM_ID)).thenThrow(new TeamNotFoundException(TEAM_ID));

        mockMvc.perform(get("/api/teams/{id}", TEAM_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Team not found"));
    }

    @Test
    void returnsDuplicateProblem() throws Exception {
        when(service.create(any(CreateTeamRequest.class))).thenThrow(new DuplicateTeamException("Platform Team"));

        var request = new CreateTeamRequest("Platform Team", "gateway", "platform@example.com", "Nadia Martin", 8);

        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Duplicate team"));
    }

    @Test
    void rejectsInvalidTeam() throws Exception {
        var body = """
                {"name":"","serviceName":"gateway","contactEmail":"bad-email","onCallEngineer":"","capacity":0}
                """;

        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request"));
    }

    private static TeamResponse response(String onCallEngineer) {
        return new TeamResponse(TEAM_ID, "Platform Team", "gateway", "platform@example.com", onCallEngineer, 8,
                Instant.parse("2026-05-22T10:00:00Z"), Instant.parse("2026-05-22T10:00:00Z"));
    }
}

