package com.six.spacex.service.mission;

import com.six.spacex.domain.SpaceXObject;
import com.six.spacex.domain.id.SpaceXId;
import com.six.spacex.service.CrudService;

public interface MissionService<ID extends SpaceXId, O extends SpaceXObject, R extends SpaceXObject>
        extends CrudService<ID, O> {
    O markAsPending(ID id, R... rockets);
    O start(ID id, R... rockets);
    O schedule(ID id, R... rockets);
    O end(ID id);
}
