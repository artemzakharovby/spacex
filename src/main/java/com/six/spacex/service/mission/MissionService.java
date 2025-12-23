package com.six.spacex.service.mission;

import com.six.spacex.domain.SpaceXObject;
import com.six.spacex.domain.id.SpaceXId;
import com.six.spacex.service.CrudService;

import java.util.List;

public interface MissionService<ID extends SpaceXId, O extends SpaceXObject, R extends SpaceXObject>
        extends CrudService<ID, O> {
    O assignRockets(ID id, List<R> rockets);
    O markAsPending(ID id, List<R> rockets);
    O start(ID id, List<R> rockets);
    O schedule(ID id, List<R> rockets);
    O end(ID id);
}
