package com.six.spacex.service.facade;

import com.six.spacex.domain.Mission;
import com.six.spacex.domain.Rocket;
import com.six.spacex.repository.MissionInMemoryRepository;
import com.six.spacex.repository.RocketInMemoryRepository;
import com.six.spacex.service.mission.DefaultMissionService;
import com.six.spacex.service.rocket.DefaultRocketService;
import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class DefaultSpaceXFacadeTest {

    private DefaultSpaceXFacade spaceXFacade;

    @Before
    public void init() {
        this.spaceXFacade = new DefaultSpaceXFacade(
                new DefaultMissionService(new MissionInMemoryRepository(new HashMap<>())),
                new DefaultRocketService(new RocketInMemoryRepository(new HashMap<>()))
        );
    }

    @Test
    public void get_all_missions_sorted_by_number_of_rockets_and_alphabetical_order() {
        Mission mars = spaceXFacade.addMission("Mars", List.of());

        Mission luna1 = spaceXFacade.addMission("Luna1", List.of());
        Rocket dragon1 = spaceXFacade.addRocket("Dragon 1");
        Rocket dragon2 = spaceXFacade.addRocket("Dragon 2");
        spaceXFacade.assignRocketsToMission(luna1.getId(), List.of(dragon1.getId(), dragon2.getId()));
        spaceXFacade.repairRocket(dragon2.getId());

        Mission doubleLanding = endedMission("Double Landing");

        Mission transit = spaceXFacade.addMission("Transit", List.of());
        Rocket redDragon = spaceXFacade.addRocket("Red dragon");
        Rocket dragonXl = spaceXFacade.addRocket("Dragon XL");
        Rocket falconHeavy = spaceXFacade.addRocket("Falcon heavy");

        spaceXFacade.assignRocketsToMission(transit.getId(), List.of(redDragon.getId(), dragonXl.getId(), falconHeavy.getId()));
        spaceXFacade.startRocket(dragonXl.getId());
        spaceXFacade.startRocket(falconHeavy.getId());

        Mission luna2 = spaceXFacade.addMission("Luna2", List.of());
        Mission verticalLanding = endedMission("Vertical Landing");

        List<Mission> expected = List.of(
                spaceXFacade.getMission(transit.getId()),
                spaceXFacade.getMission(luna1.getId()),
                spaceXFacade.getMission(verticalLanding.getId()),
                spaceXFacade.getMission(mars.getId()),
                spaceXFacade.getMission(luna2.getId()),
                spaceXFacade.getMission(doubleLanding.getId())
        );
        List<Mission> actual = spaceXFacade.getMissionsSortedBy(
                Comparator.comparing((Mission m) -> m.getRockets().size())
                        .reversed()
                        .thenComparing(Mission::getName, Comparator.reverseOrder())
        );
        assertEquals(expected, actual);
    }

    private Mission endedMission(String name) {
        Mission scheduled = spaceXFacade.addMission(name, List.of());
        Rocket rocket = spaceXFacade.addRocket("Dragon tmp");
        Mission updatedMission = spaceXFacade.assignRocketsToMission(scheduled.getId(), List.of(rocket.getId()));
        spaceXFacade.startRocket(rocket.getId());

        return spaceXFacade.endMission(updatedMission.getId());
    }
}