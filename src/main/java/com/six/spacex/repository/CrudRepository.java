package com.six.spacex.repository;

import com.six.spacex.domain.SpaceXObject;
import com.six.spacex.domain.id.SpaceXId;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<ID extends SpaceXId, O extends SpaceXObject> {
    List<O> getAll();
    Optional<O> get(ID id);
    O save(O object);
}
