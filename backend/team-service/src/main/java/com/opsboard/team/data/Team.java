package com.opsboard.team.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(nullable = false, length = 120)
    private String serviceName;

    @Column(nullable = false, length = 160)
    private String contactEmail;

    @Column(nullable = false, length = 120)
    private String onCallEngineer;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected Team() {
    }

    public Team(String name, String serviceName, String contactEmail, String onCallEngineer, int capacity) {
        this.name = name;
        this.serviceName = serviceName;
        this.contactEmail = contactEmail;
        this.onCallEngineer = onCallEngineer;
        this.capacity = capacity;
    }

    @PrePersist
    void onCreate() {
        var now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getOnCallEngineer() {
        return onCallEngineer;
    }

    public int getCapacity() {
        return capacity;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void updateOnCallEngineer(String engineer) {
        this.onCallEngineer = engineer;
    }
}

