package com.six.spacex.service;

import com.six.spacex.domain.SpaceXObject;
import com.six.spacex.domain.id.SpaceXId;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public interface CrudService<ID extends SpaceXId, O extends SpaceXObject> {
    List<O> getAll(Comparator<O> comparator);
    List<O> getAll();
    Optional<O> get(ID id);
    O save(O object);
}
