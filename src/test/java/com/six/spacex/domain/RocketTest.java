package com.six.spacex.domain;

import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class RocketTest {

    @Test
    public void rocket_created_successfully() {
        Rocket rocket = new Rocket(Optional.of(new RocketId(UUID.randomUUID())));
        assertEquals(RocketStatus.ON_GROUND, rocket.getStatus());
    }
}
