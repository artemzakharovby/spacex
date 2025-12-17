package com.six.spacex.domain;

import com.six.spacex.domain.id.MissionId;
import com.six.spacex.domain.id.RocketId;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

public class RocketTest {

    @Test
    public void rocket_cannot_be_assigned_to_mission_because_its_under_repairing() {
        Rocket rocketOnGround = new Rocket(Optional.of(new RocketId(UUID.randomUUID())), "Dragon XL");
        Rocket rocketInRepairing = rocketOnGround.markAsRepairing();
        assertThrows(InvalidObjectStateException.class, () -> {
            rocketInRepairing.markAsAssigned(new MissionId(UUID.randomUUID()));
        });
    }

    @Test
    public void rocket_can_be_assigned_to_mission() {
        Rocket rocketOnGround = new Rocket(Optional.of(new RocketId(UUID.randomUUID())), "Dragon XL");
        Rocket assignedRocket = rocketOnGround.markAsAssigned(new MissionId(UUID.randomUUID()));

        assertEquals(RocketStatus.ON_GROUND, assignedRocket.getStatus());
        assertTrue(assignedRocket.getMissionId().isPresent());
    }

    @Test
    public void rocket_cannot_be_in_space_because_its_under_repairing() {
        Rocket rocketOnGround = new Rocket(Optional.of(new RocketId(UUID.randomUUID())), "Dragon XL");
        Rocket rocketInRepairing = rocketOnGround.markAsRepairing();
        assertThrows(InvalidObjectStateException.class, rocketInRepairing::markAsInSpace);
    }

    @Test
    public void rocket_cannot_be_in_space_because_there_is_no_mission() {
        Rocket rocketOnGround = new Rocket(Optional.of(new RocketId(UUID.randomUUID())), "Dragon XL");
        assertThrows(InvalidObjectStateException.class, rocketOnGround::markAsInSpace);
    }

    @Test
    public void rocket_can_be_in_space() {
        Rocket rocketInSpace = inSpaceFlow();
        assertEquals(RocketStatus.IN_SPACE, rocketInSpace.getStatus());
    }

    @Test
    public void rocket_cannot_be_marked_as_repaired_because_its_in_space() {
        Rocket rocketInSpace = inSpaceFlow();
        assertThrows(InvalidObjectStateException.class, rocketInSpace::markAsRepaired);
    }

    @Test
    public void rocket_cannot_be_marked_as_repaired_because_its_on_ground() {
        Rocket rocketOnGround = new Rocket(Optional.of(new RocketId(UUID.randomUUID())), "Dragon XL");
        assertThrows(InvalidObjectStateException.class, rocketOnGround::markAsRepaired);
    }

    @Test
    public void rocket_cannot_be_repaired_because_its_in_space() {
        Rocket rocketInSpace = inSpaceFlow();
        assertThrows(InvalidObjectStateException.class, rocketInSpace::markAsRepairing);
    }

    @Test
    public void rocket_cannot_be_repaired_because_its_already_repairing() {
        Rocket rocketOnGround = new Rocket(Optional.of(new RocketId(UUID.randomUUID())), "Dragon XL");
        Rocket rocketInRepairing = rocketOnGround.markAsRepairing();

        assertThrows(InvalidObjectStateException.class, rocketInRepairing::markAsRepairing);
    }

    @Test
    public void rocket_was_repaired_successfully() {
        Rocket rocketOnGround = new Rocket(Optional.of(new RocketId(UUID.randomUUID())), "Dragon XL");
        Rocket rocketInRepairing = rocketOnGround.markAsRepairing();
        Rocket repairedRocket = rocketInRepairing.markAsRepaired();

        assertEquals(RocketStatus.ON_GROUND, repairedRocket.getStatus());
    }

    @Test
    public void rocket_repairing_is_successful() {
        Rocket rocketOnGround = new Rocket(Optional.of(new RocketId(UUID.randomUUID())), "Dragon XL");
        Rocket rocketInRepairing = rocketOnGround.markAsRepairing();

        assertEquals(RocketStatus.IN_REPAIR, rocketInRepairing.getStatus());
    }

    @Test
    public void cannot_create_rocket_because_name_is_null_or_blank() {
        assertThrows(InvalidObjectStateException.class, () -> {
            new Rocket(Optional.of(new RocketId(UUID.randomUUID())), null);
        });

        assertThrows(InvalidObjectStateException.class, () -> {
            new Rocket(Optional.of(new RocketId(UUID.randomUUID())), "");
        });

        assertThrows(InvalidObjectStateException.class, () -> {
            new Rocket(Optional.of(new RocketId(UUID.randomUUID())), " ");
        });

        assertThrows(InvalidObjectStateException.class, () -> {
            new Rocket(Optional.of(new RocketId(UUID.randomUUID())), "     ");
        });
    }

    @Test
    public void rocket_created_successfully() {
        Rocket rocket = new Rocket(Optional.of(new RocketId(UUID.randomUUID())), "Dragon XL");
        assertEquals(RocketStatus.ON_GROUND, rocket.getStatus());
    }

    private Rocket inSpaceFlow() {
        Rocket rocketOnGround = new Rocket(Optional.of(new RocketId(UUID.randomUUID())), "Dragon XL");
        Rocket assignedRocket = rocketOnGround.markAsAssigned(new MissionId(UUID.randomUUID()));

        return assignedRocket.markAsInSpace();
    }
}
