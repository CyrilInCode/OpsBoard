package com.opsboard.team.data;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, UUID> {

    List<Team> findByServiceNameContainingIgnoreCaseOrderByNameAsc(String serviceName);

    boolean existsByNameIgnoreCase(String name);
}

