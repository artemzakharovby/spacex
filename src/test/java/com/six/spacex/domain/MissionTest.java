package com.six.spacex.domain;

import com.six.spacex.domain.id.MissionId;
import com.six.spacex.domain.id.RocketId;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class MissionTest {

    @Test
    public void mark_mission_as_pending_which_is_already_in_progress_and_throw_InvalidObjectStateException() {
        Mission scheduled = new Mission(new MissionId(UUID.randomUUID()), "Mars");
        assertSame(MissionStatus.SCHEDULED, scheduled.getStatus());

        Rocket dragon = new Rocket(new RocketId(UUID.randomUUID()), "Dragon")
                .assignToMission(scheduled.getId());
        Mission assigned = scheduled.assignRockets(List.of(dragon));

        Rocket dragonInRepair = dragon.repair();
        Mission pending = assigned.markAsPending(List.of(dragonInRepair));
        assertSame(MissionStatus.PENDING, pending.getStatus());

        Rocket dragonRepaired = dragonInRepair.onGround();
        Mission scheduledAgain = pending.schedule(List.of(dragonRepaired));
        assertSame(MissionStatus.SCHEDULED, scheduledAgain.getStatus());

        Rocket dragonInSpace = dragonRepaired.start();
        Mission inProgress = scheduledAgain.start(List.of(dragonInSpace));
        assertSame(MissionStatus.IN_PROGRESS, inProgress.getStatus());

        assertThrows(InvalidObjectStateException.class, () -> inProgress.markAsPending(List.of()));
    }

    @Test
    public void start_mission_successfully() {
        Mission scheduled = new Mission(new MissionId(UUID.randomUUID()), "Mars");
        assertSame(MissionStatus.SCHEDULED, scheduled.getStatus());

        Rocket dragon = new Rocket(new RocketId(UUID.randomUUID()), "Dragon")
                .assignToMission(scheduled.getId());
        Mission assigned = scheduled.assignRockets(List.of(dragon));

        Rocket dragonInRepair = dragon.repair();
        Mission pending = assigned.markAsPending(List.of(dragonInRepair));
        assertSame(MissionStatus.PENDING, pending.getStatus());

        Rocket dragonRepaired = dragonInRepair.onGround();
        Mission scheduledAgain = pending.schedule(List.of(dragonRepaired));
        assertSame(MissionStatus.SCHEDULED, scheduledAgain.getStatus());

        Rocket dragonInSpace = dragonRepaired.start();
        Mission inProgress = scheduledAgain.start(List.of(dragonInSpace));
        assertSame(MissionStatus.IN_PROGRESS, inProgress.getStatus());
    }

    @Test
    public void mark_mission_as_pending_successfully() {
        Mission scheduled = new Mission(new MissionId(UUID.randomUUID()), "Mars");
        assertSame(MissionStatus.SCHEDULED, scheduled.getStatus());

        Rocket dragon = new Rocket(new RocketId(UUID.randomUUID()), "Dragon")
                .assignToMission(scheduled.getId());
        Mission assigned = scheduled.assignRockets(List.of(dragon));

        Rocket dragonInRepair = dragon.repair();
        Mission pending = assigned.markAsPending(List.of(dragonInRepair));
        assertSame(MissionStatus.PENDING, pending.getStatus());
    }

    @Test
    public void mark_mission_as_pending_without_rockets_and_throw_InvalidObjectStateException() {
        Mission scheduled = new Mission(new MissionId(UUID.randomUUID()), "Mars");
        assertSame(MissionStatus.SCHEDULED, scheduled.getStatus());

        assertThrows(InvalidObjectStateException.class, () -> scheduled.markAsPending(List.of()));
    }

    @Test
    public void create_mission_with_invalid_name_and_throw_InvalidObjectStateException() {
        assertThrows(InvalidObjectStateException.class, () -> {
            new Mission(new MissionId(UUID.randomUUID()), null);
        });

        assertThrows(InvalidObjectStateException.class, () -> {
            new Mission(new MissionId(UUID.randomUUID()), "");
        });

        assertThrows(InvalidObjectStateException.class, () -> {
            new Mission(new MissionId(UUID.randomUUID()), " ");
        });

        assertThrows(InvalidObjectStateException.class, () -> {
            new Mission(new MissionId(UUID.randomUUID()), "   ");
        });
    }

    @Test
    public void create_mission_with_invalid_id_and_throw_InvalidObjectStateException() {
        assertThrows(InvalidObjectStateException.class, () -> {
            new Mission(null, "Mars");
        });
    }

    @Test
    public void create_mission_successfully() {
        Mission mission = new Mission(new MissionId(UUID.randomUUID()), "Mars");
        assertSame(MissionStatus.SCHEDULED, mission.getStatus());
    }
}