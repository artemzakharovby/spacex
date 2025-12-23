package com.six.spacex.service.facade;

import com.six.spacex.domain.Mission;
import com.six.spacex.domain.Rocket;
import com.six.spacex.domain.id.MissionId;
import com.six.spacex.domain.id.RocketId;
import com.six.spacex.service.SpaceXServiceException;
import com.six.spacex.service.mission.MissionService;
import com.six.spacex.service.rocket.RocketService;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class DefaultSpaceXFacade implements SpaceXFacade {

    private final MissionService<MissionId, Mission, Rocket> missionService;
    private final RocketService<RocketId, Rocket, MissionId> rocketService;

    public DefaultSpaceXFacade(MissionService<MissionId, Mission, Rocket> missionService,
                               RocketService<RocketId, Rocket, MissionId> rocketService) {
        this.missionService = missionService;
        this.rocketService = rocketService;
    }

    @Override
    public Rocket addRocket(String name) {
        return rocketService.save((new Rocket(new RocketId(UUID.randomUUID()), name)));
    }

    @Override
    public Mission assignRocketsToMission(MissionId missionId, RocketId... rocketIds) {
        List<Rocket> rockets = Arrays.stream(rocketIds)
                .map(rocketId -> rocketService.get(rocketId)
                        .map(rocket -> rocket.assignToMission(missionId))
                        .orElseThrow(() -> SpaceXServiceException.notFound("rocket", rocketId)))
                .toList();

        return missionService.assignRockets(missionId, rockets);
    }

    @Override
    public Mission addMission(String name, List<Rocket> rockets) {
        return missionService.save(
                new Mission(new MissionId(UUID.randomUUID()), name, rockets)
        );
    }

    @Override
    public List<Mission> getMissionsSortedBy(Comparator<Mission> comparator) {
        return missionService.getAll(comparator);
    }
}
