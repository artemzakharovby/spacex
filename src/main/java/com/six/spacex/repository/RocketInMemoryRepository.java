package com.six.spacex.repository;

import com.six.spacex.domain.Rocket;
import com.six.spacex.domain.id.RocketId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RocketInMemoryRepository implements CrudRepository<RocketId, Rocket> {

    private final Map<RocketId, Rocket> rockets;

    public RocketInMemoryRepository(Map<RocketId, Rocket> rockets) {
        this.rockets = rockets;
    }

    @Override
    public List<Rocket> getAll() {
        return rockets.values().stream().toList();
    }

    @Override
    public Optional<Rocket> get(RocketId id) {
        return Optional.ofNullable(rockets.get(id));
    }

    @Override
    public Rocket save(Rocket rocket) {
        rockets.put(rocket.getId(), rocket);
        return rocket;
    }
}
