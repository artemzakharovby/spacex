package com.six.spacex.domain;

import com.six.spacex.InternalSpaceXException;
import com.six.spacex.domain.id.MissionId;
import com.six.spacex.domain.id.RocketId;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Mission implements SpaceXObject {

    private static final Map<MissionStatus, Set<MissionStatus>> TRANSITIONS = Map.of(
            MissionStatus.SCHEDULED, Set.of(MissionStatus.PENDING, MissionStatus.IN_PROGRESS),
            MissionStatus.PENDING, Set.of(MissionStatus.SCHEDULED),
            MissionStatus.IN_PROGRESS, Set.of(MissionStatus.IN_PROGRESS, MissionStatus.ENDED)
    );

    private final MissionId id;
    private final String name;
    private final MissionStatus status;
    private final Map<RocketId, Rocket> rockets;

    static {
        MissionStatus[] allStatuses = MissionStatus.values();
        if (TRANSITIONS.size() != allStatuses.length) {
            throw new InternalSpaceXException(
                    "Some mission statuses are not handled. Transitions: {0}, All statuses: {1}",
                    TRANSITIONS, Arrays.stream(allStatuses).toList()
            );
        }
    }

    public Mission(MissionId id, String name, List<Rocket> rockets) {
        this(id, name, MissionStatus.SCHEDULED, rockets);
    }

    private Mission(MissionId id, String name, MissionStatus status, List<Rocket> rockets) {
        this(id, name, status, rockets.stream().collect(Collectors.toMap(Rocket::getId, rocket -> rocket)));
    }

    private Mission(MissionId id, String name, MissionStatus status, Map<RocketId, Rocket> rockets) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.rockets = rockets;
        validate();
    }

    public Mission end(Rocket... rockets) {
        isTransitionAllowed(MissionStatus.ENDED);
        return changeStatus(MissionStatus.ENDED, rockets);
    }

    public Mission start(Rocket... rockets) {
        isTransitionAllowed(MissionStatus.IN_PROGRESS);
        return changeStatus(MissionStatus.IN_PROGRESS, rockets);
    }

    public Mission schedule(Rocket... rockets) {
        isTransitionAllowed(MissionStatus.SCHEDULED);
        return changeStatus(MissionStatus.SCHEDULED, rockets);
    }

    public Mission markAsPending(Rocket... rockets) {
        isTransitionAllowed(MissionStatus.PENDING);
        return changeStatus(MissionStatus.PENDING, rockets);
    }

    public Mission changeStatus(MissionStatus updatedStatus, Rocket... rockets) {
        isTransitionAllowed(updatedStatus);
        if (updatedStatus != MissionStatus.ENDED && this.rockets.size() != rockets.length) {
            throw new InvalidObjectStateException(
                    "Number of rockets has changed. Originally: {0}, now: {1}, mission: {2}",
                    this.rockets.size(), rockets.length, this
            );
        }
        for (Rocket rocket: rockets) {
            if (this.rockets.get(rocket.getId()) == null) {
                throw new InvalidObjectStateException("There is no rocket with ID {0}. Mission: {1}", rocket.getId(), this);
            }
        }
        return new Mission(id, name, updatedStatus, Arrays.stream(rockets).toList());
    }

    public MissionId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public Map<RocketId, Rocket> getRockets() {
        return rockets;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Mission mission = (Mission) o;
        return Objects.equals(id, mission.id) && Objects.equals(name, mission.name)
                && status == mission.status && Objects.equals(rockets, mission.rockets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status, rockets);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Mission{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", status=").append(status);
        sb.append(", rockets=").append(rockets);
        sb.append('}');
        return sb.toString();
    }

    private void validate() {
        if (id == null) {
            throw new InvalidObjectStateException("Mission ID cannot be null. Mission: {0}", this);
        }
        if (name == null || name.isBlank()) {
            throw new InvalidObjectStateException("Mission name cannot be null or blank. Mission: {0}", this);
        }
        if (status == null) {
            throw new InvalidObjectStateException("Mission status cannot be null. Mission: {0}", this);
        }
        if (rockets == null) {
            throw new InvalidObjectStateException("Mission rockets cannot be null. Mission: {0}", this);
        }
        if (status == MissionStatus.IN_PROGRESS && noRocketIsInSpace()) {
            throw new InvalidObjectStateException(
                    "Mission cannot be in progress, there is no rocket in space. Mission: {0}", this
            );
        }
    }

    private void isTransitionAllowed(MissionStatus updatedStatus) {
        if (!TRANSITIONS.get(status).contains(updatedStatus)) {
            throw new InvalidObjectStateException(
                    "Mission status cannot be updated from {0} to {1}. Rocket: {2}", status, updatedStatus, this
            );
        }
    }

    private boolean noRocketIsInSpace() {
        return rockets.values()
                .stream()
                .noneMatch(rocket -> rocket.getStatus() == RocketStatus.IN_SPACE);
    }
}
