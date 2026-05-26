package com.opsboard.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;

@SpringBootTest
class GatewayApplicationTest {

    @Autowired
    private RouteLocator routeLocator;

    @Test
    void loadsConfiguredRoutes() {
        var routeIds = routeLocator.getRoutes()
                .map(route -> route.getId())
                .collectList()
                .block();

        assertThat(routeIds).contains("incident-service", "team-service");
    }
}

