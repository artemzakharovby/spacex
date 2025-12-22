package com.six.spacex.repository;

import com.six.spacex.domain.id.MissionId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MissionInMemoryRepository implements CrudRepository<MissionId, Mission> {

    private final Map<MissionId, Mission> missions;

    public MissionInMemoryRepository(Map<MissionId, Mission> missions) {
        this.missions = missions;
    }

    @Override
    public List<Mission> getAll() {
        return missions.values().stream().toList();
    }

    @Override
    public Optional<Mission> get(MissionId id) {
        return Optional.ofNullable(missions.get(id));
    }

    @Override
    public Mission save(Mission mission) {
        missions.put(mission.getId(), mission);
        return mission;
    }
}
