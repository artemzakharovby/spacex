package com.six.spacex.domain;

import com.six.spacex.domain.id.MissionId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Mission implements SpaceXObject {

    private final Optional<MissionId> id;
    private final MissionStatus status;
    private final List<Rocket> rockets;

    public Mission(Optional<MissionId> id) {
        this(id, MissionStatus.SCHEDULED, new ArrayList<>());
    }

    private Mission(Optional<MissionId> id, MissionStatus status, List<Rocket> rockets) {
        this.id = id;
        this.status = status;
        this.rockets = rockets;
        validate();
    }

    public Optional<MissionId> getId() {
        return id;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public List<Rocket> getRockets() {
        return rockets;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Mission mission = (Mission) o;
        return Objects.equals(id, mission.id) && status == mission.status && Objects.equals(rockets, mission.rockets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, rockets);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Mission{");
        sb.append("id=").append(id);
        sb.append(", status=").append(status);
        sb.append(", rockets=").append(rockets);
        sb.append('}');
        return sb.toString();
    }

    private void validate() {
        if (id == null) {
            throw new InvalidObjectStateException("Mission ID cannot be null, mission: {0}", this);
        }
        if (status == null) {
            throw new InvalidObjectStateException("Mission status cannot be null, mission: {0}", this);
        }
        if (rockets == null) {
            throw new InvalidObjectStateException("Rockets cannot be null, mission: {0}", this);
        }
    }
}
