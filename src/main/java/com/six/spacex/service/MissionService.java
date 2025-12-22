package com.six.spacex.service;

import com.six.spacex.domain.SpaceXObject;
import com.six.spacex.domain.id.SpaceXId;

import java.util.List;

public interface MissionService<ID extends SpaceXId, O extends SpaceXObject> extends CrudService<ID, O> {
    List<O> getAllMissionsSortedByNumberOfRockets();
}
