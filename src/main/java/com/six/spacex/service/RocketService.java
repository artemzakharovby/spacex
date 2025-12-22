package com.six.spacex.service;

import com.six.spacex.domain.SpaceXObject;
import com.six.spacex.domain.id.SpaceXId;

public interface RocketService<ID extends SpaceXId, O extends SpaceXObject, MID extends SpaceXId>
        extends CrudService<ID, O> {
    O assignToMission(ID rocketId, MID missionId);
    O repair(ID rocketId);
    O putOnGround(ID rocketId);
    O start(ID rocketId);
}
