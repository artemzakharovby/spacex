package com.six.spacex.service.facade;

import com.six.spacex.domain.Mission;
import com.six.spacex.domain.Rocket;
import com.six.spacex.domain.id.MissionId;
import com.six.spacex.domain.id.RocketId;

import java.util.Comparator;
import java.util.List;

public interface SpaceXFacade {
    Rocket addRocket(String name);
    Mission assignRocketsToMission(MissionId missionId, RocketId... rocketIds);
    Mission addMission(String name, List<Rocket> rockets);
    List<Mission> getMissionsSortedBy(Comparator<Mission> comparator);
}
