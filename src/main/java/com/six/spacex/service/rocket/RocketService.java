package com.six.spacex.service.rocket;

import com.six.spacex.domain.SpaceXObject;
import com.six.spacex.domain.id.SpaceXId;
import com.six.spacex.service.CrudService;

import java.util.List;

public interface RocketService<ID extends SpaceXId, O extends SpaceXObject, MID extends SpaceXId>
        extends CrudService<ID, O> {
    List<O> getRocketsByMissionId(MID missionId);
    O assignToMission(ID rocketId, MID missionId);
    O repair(ID rocketId);
    O putOnGround(ID rocketId);
    O start(ID rocketId);
}
