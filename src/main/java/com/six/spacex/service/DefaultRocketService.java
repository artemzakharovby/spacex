package com.six.spacex.service;

import com.six.spacex.domain.Rocket;
import com.six.spacex.domain.id.MissionId;
import com.six.spacex.domain.id.RocketId;
import com.six.spacex.repository.CrudRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class DefaultRocketService implements RocketService<RocketId, Rocket, MissionId> {

    private final CrudRepository<RocketId, Rocket> rocketRepository;

    public DefaultRocketService(CrudRepository<RocketId, Rocket> rocketRepository) {
        this.rocketRepository = rocketRepository;
    }

    @Override
    public Rocket assignToMission(RocketId rocketId, MissionId missionId) {
        return updateRocketOrThrow(rocketId, () -> rocket -> rocket.assignToMission(missionId));
    }

    @Override
    public Rocket repair(RocketId id) {
        return updateRocketOrThrow(id, () -> Rocket::repair);
    }

    @Override
    public Rocket putOnGround(RocketId id) {
        return updateRocketOrThrow(id, () -> Rocket::onGround);
    }

    @Override
    public Rocket start(RocketId id) {
        return updateRocketOrThrow(id, () -> Rocket::start);
    }

    @Override
    public List<Rocket> getAll(Comparator<Rocket> comparator) {
        return getAll().stream().sorted(comparator).toList();
    }

    @Override
    public List<Rocket> getAll() {
        return rocketRepository.getAll();
    }

    @Override
    public Optional<Rocket> get(RocketId id) {
        return rocketRepository.get(id);
    }

    @Override
    public Rocket save(Rocket rocket) {
        return rocketRepository.save(rocket);
    }

    private Rocket updateRocketOrThrow(RocketId rocketId, Supplier<Function<Rocket, Rocket>> operation) {
        return rocketRepository.get(rocketId)
                .map(operation.get())
                .map(rocketRepository::save)
                .orElseThrow(() -> new SpaceXServiceException("There is no rocket with ID {0}", rocketId));
    }
}
