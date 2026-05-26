package com.opsboard.incident.controller;

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
import com.opsboard.incident.data.IncidentSeverity;
import com.opsboard.incident.data.IncidentStatus;
import com.opsboard.incident.dto.CreateIncidentRequest;
import com.opsboard.incident.dto.DashboardResponse;
import com.opsboard.incident.dto.IncidentResponse;
import com.opsboard.incident.dto.UpdateIncidentStatusRequest;
import com.opsboard.incident.service.IncidentNotFoundException;
import com.opsboard.incident.service.IncidentService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(IncidentController.class)
class IncidentControllerTest {

    private static final UUID INCIDENT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID TEAM_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IncidentService service;

    @Test
    void listsIncidents() throws Exception {
        when(service.findAll(any())).thenReturn(List.of(response(IncidentStatus.OPEN)));

        mockMvc.perform(get("/api/incidents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("API latency above SLO"))
                .andExpect(jsonPath("$[0].status").value("OPEN"));
    }

    @Test
    void createsIncident() throws Exception {
        when(service.create(any(CreateIncidentRequest.class))).thenReturn(response(IncidentStatus.OPEN));

        var body = new CreateIncidentRequest("API latency above SLO", "p95 over SLO", IncidentSeverity.HIGH, TEAM_ID);

        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/incidents/" + INCIDENT_ID))
                .andExpect(jsonPath("$.severity").value("HIGH"));
    }

    @Test
    void updatesStatus() throws Exception {
        when(service.updateStatus(eq(INCIDENT_ID), any(UpdateIncidentStatusRequest.class)))
                .thenReturn(response(IncidentStatus.RESOLVED));

        mockMvc.perform(patch("/api/incidents/{id}/status", INCIDENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateIncidentStatusRequest(IncidentStatus.RESOLVED))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESOLVED"));
    }

    @Test
    void returnsNotFoundProblem() throws Exception {
        when(service.getById(INCIDENT_ID)).thenThrow(new IncidentNotFoundException(INCIDENT_ID));

        mockMvc.perform(get("/api/incidents/{id}", INCIDENT_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Incident not found"));
    }

    @Test
    void returnsDashboard() throws Exception {
        when(service.dashboard()).thenReturn(new DashboardResponse(3, 2, 1, 0, 1));

        mockMvc.perform(get("/api/incidents/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(3))
                .andExpect(jsonPath("$.critical").value(1));
    }

    @Test
    void rejectsInvalidCreateRequest() throws Exception {
        var body = """
                {"title":"","description":"","severity":"HIGH","teamId":"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"}
                """;

        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request"));
    }

    private static IncidentResponse response(IncidentStatus status) {
        return new IncidentResponse(INCIDENT_ID, "API latency above SLO", "p95 over SLO", IncidentSeverity.HIGH, status,
                TEAM_ID, Instant.parse("2026-05-22T10:00:00Z"), Instant.parse("2026-05-22T10:00:00Z"));
    }
}

