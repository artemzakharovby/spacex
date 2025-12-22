package com.six.spacex.domain;

import com.six.spacex.domain.id.MissionId;
import com.six.spacex.domain.id.RocketId;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class MissionTest {

    private static final List<String> MISSION_NAMES = List.of(
            "Mars", "Luna1", "Double Landing", "Transit", "Luna2", "Vertical Landing"
    );

    @Test
    public void e2e_test_scheduled_pending_scheduled_pending_scheduled_in_progress_ended() {
        RocketId rocketId = new RocketId(UUID.randomUUID());
        Rocket rocket1 = new Rocket(rocketId, "Dragon 1");

        Mission initial = new Mission(new MissionId(UUID.randomUUID()), "Mars", List.of(rocket1));
        assertSame(MissionStatus.SCHEDULED, initial.getStatus());

        Mission rocket1IsRepairing = initial.markAsPending(rocketId);
        assertSame(MissionStatus.PENDING, rocket1IsRepairing.getStatus());

        Mission rocket1IsRepaired = rocket1IsRepairing.markAsScheduled();
        assertSame(MissionStatus.SCHEDULED, rocket1IsRepaired.getStatus());

        RocketId rocketId2 = new RocketId(UUID.randomUUID());
        Rocket rocket2 = new Rocket(rocketId2, "Dragon 2");
        assertTrue(rocket2.getMissionId().isEmpty());

        Mission missionWithAdditionalRocket = rocket1IsRepaired.assignRockets(rocket2);
        assertSame(MissionStatus.SCHEDULED, missionWithAdditionalRocket.getStatus());
        assertTrue(missionWithAdditionalRocket.getRockets().get(rocketId2).getMissionId().isPresent());

        Mission rocket2IsRepairing = missionWithAdditionalRocket.markAsPending(rocketId2);
        assertSame(MissionStatus.PENDING, rocket2IsRepairing.getStatus());

        Mission finallyScheduled = rocket2IsRepairing.markAsScheduled();
        assertSame(MissionStatus.SCHEDULED, finallyScheduled.getStatus());

        Mission inProgress = finallyScheduled.markAsInProgress();
        assertSame(MissionStatus.IN_PROGRESS, inProgress.getStatus());

        Mission ended = inProgress.markAsEnded();
        assertSame(MissionStatus.ENDED, ended.getStatus());
    }

    @Test
    public void mark_mission_as_in_progress_which_is_pending() {
        RocketId rocketId = new RocketId(UUID.randomUUID());
        Rocket rocket1 = new Rocket(rocketId, "Dragon 1");

        Mission pending = new Mission(new MissionId(UUID.randomUUID()), "Mars", List.of(rocket1))
                .markAsPending(rocketId);

        assertThrows(InvalidObjectStateException.class, pending::markAsInProgress);
    }

    @Test
    public void mark_mission_as_in_progress_which_is_already_ended() {
        RocketId rocketId = new RocketId(UUID.randomUUID());
        Rocket rocket1 = new Rocket(rocketId, "Dragon 1");

        Mission ended = new Mission(new MissionId(UUID.randomUUID()), "Mars", List.of(rocket1))
                .markAsInProgress()
                .markAsEnded();

        assertThrows(InvalidObjectStateException.class, ended::markAsInProgress);
    }

    @Test
    public void mark_mission_as_in_progress_which_is_already_in_progress_and_throw_exception() {
        RocketId rocketId = new RocketId(UUID.randomUUID());
        Rocket rocket1 = new Rocket(rocketId, "Dragon 1");

        Mission inProgress = new Mission(new MissionId(UUID.randomUUID()), "Mars", List.of(rocket1))
                .markAsInProgress();
        assertThrows(InvalidObjectStateException.class, inProgress::markAsInProgress);
    }

    @Test
    public void mark_mission_as_in_progress() {
        RocketId rocketId = new RocketId(UUID.randomUUID());
        Rocket rocket1 = new Rocket(rocketId, "Dragon 1");

        Mission inProgress = new Mission(new MissionId(UUID.randomUUID()), "Mars", List.of(rocket1))
                .markAsInProgress();
        assertSame(MissionStatus.IN_PROGRESS, inProgress.getStatus());
    }

    @Test
    public void mark_mission_back_as_scheduled_which_is_already_scheduled() {
        RocketId rocketId = new RocketId(UUID.randomUUID());
        Rocket rocket1 = new Rocket(rocketId, "Dragon 1");

        Mission inProgress = new Mission(new MissionId(UUID.randomUUID()), "Mars", List.of(rocket1))
                .markAsPending(rocketId)
                .markAsScheduled();

        assertThrows(InvalidObjectStateException.class, inProgress::markAsScheduled);
    }

    @Test
    public void mark_mission_back_as_scheduled_while_its_in_progress_and_throw_exception() {
        RocketId rocketId = new RocketId(UUID.randomUUID());
        Rocket rocket1 = new Rocket(rocketId, "Dragon 1");

        Mission inProgress = new Mission(new MissionId(UUID.randomUUID()), "Mars", List.of(rocket1))
                .markAsInProgress();

        assertThrows(InvalidObjectStateException.class, inProgress::markAsScheduled);
    }

    @Test
    public void mark_mission_back_as_scheduled() {
        RocketId rocketId = new RocketId(UUID.randomUUID());
        Rocket rocket1 = new Rocket(rocketId, "Dragon 1");
        RocketId rocketId2 = new RocketId(UUID.randomUUID());
        Rocket rocket2 = new Rocket(rocketId2, "Dragon 2");

        Mission scheduled = new Mission(new MissionId(UUID.randomUUID()), "Mars", List.of(rocket1, rocket2))
                .markAsPending(rocketId)
                .markAsScheduled();

        assertSame(MissionStatus.SCHEDULED, scheduled.getStatus());
    }

    @Test
    public void mark_mission_as_pending_which_is_ended_and_throw_exception() {
        RocketId rocketId = new RocketId(UUID.randomUUID());
        Rocket rocket1 = new Rocket(rocketId, "Dragon 1");

        Mission ended = new Mission(new MissionId(UUID.randomUUID()), "Mars", List.of(rocket1))
                .markAsInProgress()
                .markAsEnded();

        assertThrows(InvalidObjectStateException.class, () -> ended.markAsPending(new RocketId(UUID.randomUUID())));
    }

    @Test
    public void mark_mission_as_pending_which_is_in_progress_and_throw_exception() {
        RocketId rocketId = new RocketId(UUID.randomUUID());
        Rocket rocket1 = new Rocket(rocketId, "Dragon 1");

        Mission missionInProgress = new Mission(new MissionId(UUID.randomUUID()), "Mars", List.of(rocket1))
                .markAsInProgress();

        assertThrows(InvalidObjectStateException.class, () -> missionInProgress.markAsPending(new RocketId(UUID.randomUUID())));
    }

    @Test
    public void mark_mission_as_pending_without_rockets_and_throw_exception() {
        Mission mission = new Mission(new MissionId(UUID.randomUUID()), "Mars");
        assertThrows(InvalidObjectStateException.class, () -> mission.markAsPending(new RocketId(UUID.randomUUID())));
    }

    @Test
    public void mark_mission_as_pending_with_not_existing_rocket_id_and_throw_exception() {
        RocketId rocketId = new RocketId(UUID.randomUUID());
        Rocket rocket1 = new Rocket(rocketId, "Dragon 1");

        Mission mission = new Mission(new MissionId(UUID.randomUUID()), "Mars", List.of(rocket1));
        assertThrows(InvalidObjectStateException.class, () -> mission.markAsPending(new RocketId(UUID.randomUUID())));
    }

    @Test
    public void mark_mission_as_pending_successfully() {
        RocketId rocketId = new RocketId(UUID.randomUUID());
        Rocket rocket1 = new Rocket(rocketId, "Dragon 1");

        Mission pendingMission = new Mission(new MissionId(UUID.randomUUID()), "Mars", List.of(rocket1))
                .markAsPending(rocketId);

        assertSame(MissionStatus.PENDING, pendingMission.getStatus());
    }

    @Test
    public void assign_zero_rockets_to_mission_and_throw_exception() {
        Rocket rocket1 = new Rocket(new RocketId(UUID.randomUUID()), "Dragon 1");
        Mission mission = new Mission(new MissionId(UUID.randomUUID()), "Mars", List.of(rocket1));

        assertThrows(InvalidObjectStateException.class, mission::assignRockets);
    }

    @Test
    public void assign_rocket_to_ended_mission_and_throw_exception() {
        Rocket rocket1 = new Rocket(new RocketId(UUID.randomUUID()), "Dragon 1");
        Mission mission = new Mission(new MissionId(UUID.randomUUID()), "Mars", List.of(rocket1));

        Rocket rocket2 = new Rocket(new RocketId(UUID.randomUUID()), "Dragon 2");
        Mission missionInProgress = mission.markAsInProgress();
        Mission endedMission = missionInProgress.markAsEnded();

        assertThrows(InvalidObjectStateException.class, () -> endedMission.assignRockets(rocket2));
    }

    @Test
    public void assign_rocket_to_mission_in_progress_and_throw_exception() {
        Rocket rocket1 = new Rocket(new RocketId(UUID.randomUUID()), "Dragon 1");
        Mission mission = new Mission(new MissionId(UUID.randomUUID()), "Mars", List.of(rocket1));

        Rocket rocket2 = new Rocket(new RocketId(UUID.randomUUID()), "Dragon 2");
        Mission missionInProgress = mission.markAsInProgress();
        assertThrows(InvalidObjectStateException.class, () -> missionInProgress.assignRockets(rocket2));
    }

    @Test
    public void assign_rocket_to_mission_again_and_throw_exception() {
        Rocket rocket1 = new Rocket(new RocketId(UUID.randomUUID()), "Dragon 1");
        Mission mission = new Mission(new MissionId(UUID.randomUUID()), "Mars", List.of(rocket1));

        assertThrows(InvalidObjectStateException.class, () -> mission.assignRockets(rocket1));
    }

    @Test
    public void rockets_assigned_to_mission() {
        Rocket rocket1 = new Rocket(new RocketId(UUID.randomUUID()), "Dragon 1");
        Rocket rocket2 = new Rocket(new RocketId(UUID.randomUUID()), "Dragon 2");

        Mission mission = new Mission(new MissionId(UUID.randomUUID()), "Mars");
        Mission missionWithRockets = mission.assignRockets(rocket1, rocket2);

        missionWithRockets.getRockets().forEach((id, rocket) -> {
            assertTrue(rocket.getMissionId().isPresent());
        });
    }

    @Test
    public void rockets_passed_to_mission_during_creation_are_assigned() {
        Rocket rocket1 = new Rocket(new RocketId(UUID.randomUUID()), "Dragon 1");
        Rocket rocket2 = new Rocket(new RocketId(UUID.randomUUID()), "Dragon 1");

        Mission mission = new Mission(new MissionId(UUID.randomUUID()), "Mars", List.of(rocket1, rocket2));
        mission.getRockets().forEach((id, rocket) -> {
            assertTrue(rocket.getMissionId().isPresent());
        });
    }

    @Test
    public void cannot_create_mission_because_name_is_null_or_blank() {
        assertThrows(InvalidObjectStateException.class, () -> new Mission(new MissionId(UUID.randomUUID()), null));
        assertThrows(InvalidObjectStateException.class, () -> new Mission(new MissionId(UUID.randomUUID()), ""));
        assertThrows(InvalidObjectStateException.class, () -> new Mission(new MissionId(UUID.randomUUID()), " "));
        assertThrows(InvalidObjectStateException.class, () -> new Mission(new MissionId(UUID.randomUUID()), "    "));
    }

    @Test
    public void mission_created_successfully() {
        Mission mission = new Mission(new MissionId(UUID.randomUUID()), "Mars");
        assertEquals(MissionStatus.SCHEDULED, mission.getStatus());
    }
}
