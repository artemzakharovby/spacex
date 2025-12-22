package com.six.spacex.domain;

import com.six.spacex.domain.id.MissionId;
import com.six.spacex.domain.id.RocketId;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Rocket implements SpaceXObject {

    private static final Map<RocketStatus, Set<RocketStatus>> TRANSITIONS = Map.of(
            RocketStatus.ON_GROUND, Set.of(RocketStatus.IN_REPAIR, RocketStatus.IN_SPACE),
            RocketStatus.IN_REPAIR, Set.of(RocketStatus.ON_GROUND),
            RocketStatus.IN_SPACE, Set.of(RocketStatus.ON_GROUND)
    );

    private final RocketId id;
    private final String name;
    private final RocketStatus status;
    private final Optional<MissionId> missionId;

    static {
       RocketStatus[] allStatuses = RocketStatus.values();
       if (TRANSITIONS.size() != allStatuses.length) {
           throw new InvalidObjectStateException(
                   "Some rocket statuses are not handled. Transitions: {0}, All statuses: {1}",
                   TRANSITIONS, Arrays.stream(allStatuses).toList()
           );
       }
    }

    public Rocket(RocketId id, String name) {
        this(id, name, RocketStatus.ON_GROUND, Optional.empty());
    }

    private Rocket(RocketId id, String name, RocketStatus status, Optional<MissionId> missionId) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.missionId = missionId;
        validate();
    }

    public Rocket start() {
        isTransitionAllowed(RocketStatus.IN_SPACE);
        if (missionId.isEmpty()) {
            throw new InvalidObjectStateException("Rocket cannot go to space because there is no mission. Rocket: {0}", this);
        }
        return new Rocket(id, name, RocketStatus.IN_SPACE, missionId);
    }

    public Rocket onGround() {
        isTransitionAllowed(RocketStatus.ON_GROUND);
        return new Rocket(id, name, RocketStatus.ON_GROUND, missionId);
    }

    public Rocket repair() {
        isTransitionAllowed(RocketStatus.IN_REPAIR);
        return new Rocket(id, name, RocketStatus.IN_REPAIR, missionId);
    }

    public Rocket assignToMission(MissionId missionId) {
        return new Rocket(id, name, status, Optional.of(missionId));
    }

    public RocketId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public RocketStatus getStatus() {
        return status;
    }

    public Optional<MissionId> getMissionId() {
        return missionId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Rocket rocket = (Rocket) o;
        return Objects.equals(id, rocket.id) && Objects.equals(name, rocket.name)
                && status == rocket.status && Objects.equals(missionId, rocket.missionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status, missionId);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Rocket{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", status=").append(status);
        sb.append(", missionId=").append(missionId);
        sb.append('}');
        return sb.toString();
    }

    private void isTransitionAllowed(RocketStatus updatedStatus) {
        if (!TRANSITIONS.get(status).contains(updatedStatus)) {
            throw new InvalidObjectStateException(
                    "Rocket status cannot be updated from {0} to {1}. Rocket: {2}", status, updatedStatus, this
            );
        }
    }

    private void validate() {
        if (id == null) {
            throw new InvalidObjectStateException("Rocket ID cannot be null. Rocket: {0}", this);
        }
        if (name == null || name.isBlank()) {
            throw new InvalidObjectStateException("Rocket name cannot be null or blank. Rocket: {0}", this);
        }
        if (status == null) {
            throw new InvalidObjectStateException("Rocket status cannot be null. Rocket: {0}", this);
        }
        if (status == RocketStatus.IN_SPACE && missionId.isEmpty()) {
            throw new InvalidObjectStateException("Rocket is IN_SPACE and there is no mission assigned. Rocket: {0}", this);
        }
    }
}
