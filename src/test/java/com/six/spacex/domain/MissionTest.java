package com.six.spacex.domain;

import com.six.spacex.domain.id.MissionId;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class MissionTest {

    @Test
    public void mission_created_successfully() {
        Mission mission = new Mission(Optional.of(new MissionId(UUID.randomUUID())), "Mars", List.of());
        assertEquals(MissionStatus.SCHEDULED, mission.getStatus());
    }
}
