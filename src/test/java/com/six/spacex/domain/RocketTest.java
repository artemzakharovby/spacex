package com.six.spacex.domain;

import com.six.spacex.domain.id.MissionId;
import com.six.spacex.domain.id.RocketId;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class RocketTest {

    @Test
    public void rocket_went_from_space_and_landed() {
        Rocket onGround = new Rocket(new RocketId(UUID.randomUUID()), "Dragon 2");
        assertSame(RocketStatus.ON_GROUND, onGround.getStatus());

        Rocket inSpace = onGround.assignToMission(new MissionId(UUID.randomUUID()))
                .start();
        assertSame(RocketStatus.IN_SPACE, inSpace.getStatus());

        Rocket landed = inSpace.onGround();
        assertSame(RocketStatus.ON_GROUND, landed.getStatus());
    }

    @Test
    public void rocket_starts_while_its_repairing_and_throw_InvalidObjectStateException() {
        Rocket onGround = new Rocket(new RocketId(UUID.randomUUID()), "Dragon 2");
        assertSame(RocketStatus.ON_GROUND, onGround.getStatus());

        Rocket repairing = onGround.assignToMission(new MissionId(UUID.randomUUID()))
                .repair();
        assertSame(RocketStatus.IN_REPAIR, repairing.getStatus());

        assertThrows(InvalidObjectStateException.class, repairing::start);
    }

    @Test
    public void rocket_starts_without_mission_and_throw_InvalidObjectStateException() {
        Rocket onGround = new Rocket(new RocketId(UUID.randomUUID()), "Dragon 2");
        assertSame(RocketStatus.ON_GROUND, onGround.getStatus());

        assertThrows(InvalidObjectStateException.class, onGround::start);
    }

    @Test
    public void rocket_repaired_and_returned_on_ground_successfully() {
        Rocket onGround = new Rocket(new RocketId(UUID.randomUUID()), "Dragon 2");
        assertSame(RocketStatus.ON_GROUND, onGround.getStatus());

        Rocket inRepair = onGround.repair();
        assertSame(RocketStatus.IN_REPAIR, inRepair.getStatus());

        Rocket repaired = inRepair.onGround();
        assertSame(RocketStatus.ON_GROUND, repaired.getStatus());
    }

    @Test
    public void repair_rocket_which_is_already_in_space_and_throw_InvalidObjectStateException() {
        Rocket onGround = new Rocket(new RocketId(UUID.randomUUID()), "Dragon 2");
        assertSame(RocketStatus.ON_GROUND, onGround.getStatus());

        Rocket assigned = onGround.assignToMission(new MissionId(UUID.randomUUID()));
        Rocket inSpace = assigned.start();
        assertSame(RocketStatus.IN_SPACE, inSpace.getStatus());

        assertThrows(InvalidObjectStateException.class, inSpace::repair);
    }

    @Test
    public void repair_rocket_successfully() {
        Rocket onGround = new Rocket(new RocketId(UUID.randomUUID()), "Dragon 2");
        assertSame(RocketStatus.ON_GROUND, onGround.getStatus());

        Rocket inRepair = onGround.repair();
        assertSame(RocketStatus.IN_REPAIR, inRepair.getStatus());
    }

    @Test
    public void create_rocket_with_invalid_name_and_throw_InvalidObjectStateException() {
        assertThrows(InvalidObjectStateException.class, () -> {
            new Rocket(new RocketId(UUID.randomUUID()), null);
        });

        assertThrows(InvalidObjectStateException.class, () -> {
            new Rocket(new RocketId(UUID.randomUUID()), "");
        });

        assertThrows(InvalidObjectStateException.class, () -> {
            new Rocket(new RocketId(UUID.randomUUID()), " ");
        });

        assertThrows(InvalidObjectStateException.class, () -> {
            new Rocket(new RocketId(UUID.randomUUID()), "     ");
        });
    }

    @Test
    public void create_rocket_with_invalid_id_and_throw_InvalidObjectStateException() {
        assertThrows(InvalidObjectStateException.class, () -> {
            new Rocket(null, "Dragon 2");
        });
    }

    @Test
    public void create_rocket_successfully() {
        Rocket rocket = new Rocket(new RocketId(UUID.randomUUID()), "Dragon 2");
        assertSame(RocketStatus.ON_GROUND, rocket.getStatus());
    }
}