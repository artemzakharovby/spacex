package com.six.spacex.service;

import com.six.spacex.domain.Mission;
import com.six.spacex.domain.id.MissionId;
import com.six.spacex.repository.CrudRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class DefaultMissionService implements MissionService<MissionId, Mission> {

    private final CrudRepository<MissionId, Mission> missionRepository;

    public DefaultMissionService(CrudRepository<MissionId, Mission> missionRepository) {
        this.missionRepository = missionRepository;
    }

    @Override
    public List<Mission> getAllMissionsSortedByNumberOfRockets() {
        return missionRepository.getAll()
                .stream()
                .sorted(
                        Comparator.comparingInt(Mission::getRocketsNumber)
                                .reversed()
                                .thenComparing(Mission::getName)
                )
                .toList();
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
}
