package com.six.spacex.service.mission;

import com.six.spacex.domain.Mission;
import com.six.spacex.domain.Rocket;
import com.six.spacex.domain.id.MissionId;
import com.six.spacex.repository.CrudRepository;
import com.six.spacex.service.SpaceXServiceException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class DefaultMissionService implements MissionService<MissionId, Mission, Rocket> {

    private final CrudRepository<MissionId, Mission> missionRepository;

    public DefaultMissionService(CrudRepository<MissionId, Mission> missionRepository) {
        this.missionRepository = missionRepository;
    }

    @Override
    public Mission markAsPending(MissionId id, Rocket... rockets) {
        return updateMissionOrThrow(id, () -> mission -> mission.markAsPending(rockets));
    }

    @Override
    public Mission start(MissionId id, Rocket... rockets) {
        return updateMissionOrThrow(id, () -> mission -> mission.start(rockets));
    }

    @Override
    public Mission schedule(MissionId id, Rocket... rockets) {
        return updateMissionOrThrow(id, () -> mission -> mission.schedule(rockets));
    }

    @Override
    public Mission end(MissionId id) {
        return updateMissionOrThrow(id, () -> Mission::end);
    }

    @Override
    public List<Mission> getAll(Comparator<Mission> comparator) {
        return getAll().stream().sorted(comparator).toList();
    }

    @Override
    public List<Mission> getAll() {
        return missionRepository.getAll();
    }

    @Override
    public Optional<Mission> get(MissionId id) {
        return missionRepository.get(id);
    }

    @Override
    public Mission save(Mission mission) {
        return missionRepository.save(mission);
    }

    private Mission updateMissionOrThrow(MissionId id, Supplier<Function<Mission, Mission>> operation) {
        return missionRepository.get(id)
                .map(operation.get())
                .map(missionRepository::save)
                .orElseThrow(() -> new SpaceXServiceException("There is no mission with ID {0}", id));
    }
}
