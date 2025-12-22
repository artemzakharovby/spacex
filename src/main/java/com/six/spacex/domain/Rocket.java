package com.six.spacex.domain;

import com.six.spacex.InternalSpaceXException;
import com.six.spacex.domain.id.MissionId;
import com.six.spacex.domain.id.RocketId;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Rocket implements SpaceXObject {

    private static final Map<RocketStatus, Set<RocketStatus>> TRANSITIONS = Map.of(
            RocketStatus.ON_GROUND, Set.of(RocketStatus.IN_SPACE, RocketStatus.IN_REPAIR),
            RocketStatus.IN_REPAIR, Set.of(RocketStatus.ON_GROUND),
            RocketStatus.IN_SPACE, Set.of(RocketStatus.ON_GROUND)
    );

    static {
        RocketStatus[] rocketStatuses = RocketStatus.values();
        if (TRANSITIONS.size() < rocketStatuses.length) {
            throw new InternalSpaceXException(
                    "Not all rocket statuses are handled. TRANSITIONS: {0}, RocketStatuses: {1}",
                    TRANSITIONS, Arrays.stream(rocketStatuses).toList()
            );
        }
    }

    private final RocketId id;
    private final String name;
    private final RocketStatus status;
    private final Optional<MissionId> missionId;

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

    public Rocket markAsOnGround() {
        isTransitionAllowed(status, RocketStatus.ON_GROUND);
        return new Rocket(id, name);
    }

    public Rocket markAsInSpace() {
        validateBeforeGoingToSpace();
        return new Rocket(id, name, RocketStatus.IN_SPACE, missionId);
    }

    public Rocket markAsRepaired() {
        isTransitionAllowed(status, RocketStatus.ON_GROUND);
        return new Rocket(id, name, RocketStatus.ON_GROUND, missionId);
    }

    public Rocket markAsRepairing() {
        isTransitionAllowed(status, RocketStatus.IN_REPAIR);
        return new Rocket(id, name, RocketStatus.IN_REPAIR, missionId);
    }

    public Rocket markAsAssigned(MissionId missionId) {
        validateBeforeAssignment(missionId);
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
        return Objects.equals(id, rocket.id) && Objects.equals(name, rocket.name) && status == rocket.status
                && Objects.equals(missionId, rocket.missionId);
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

    private void validateBeforeGoingToSpace() {
        isTransitionAllowed(status, RocketStatus.IN_SPACE);
        if (missionId.isEmpty()) {
            throw new InvalidObjectStateException("Rocket cannot be in space because there is no mission. Rocker: {0}", this);
        }
    }

    private void isTransitionAllowed(RocketStatus current, RocketStatus updated) {
        Set<RocketStatus> allowedTransitions = TRANSITIONS.get(current);
        if (!allowedTransitions.contains(updated)) {
            throw new InvalidObjectStateException(
                    "Rocket status {0} cannot be changed to {1}. Available statuses: {2}", current, updated, allowedTransitions
            );
        }
    }

    private void validateBeforeAssignment(MissionId missionId) {
        if (status != RocketStatus.ON_GROUND) {
            throw new InvalidObjectStateException(
                    "Rocket cannot be assigned to mission because of invalid status. Rocket: {0}", this
            );
        }
        if (this.missionId.isPresent()) {
            throw new InvalidObjectStateException("Rocket is already assigned to mission. Rocket: {0}", this);
        }
        if (missionId == null) {
            throw new InvalidObjectStateException("Rocket cannot be assigned to mission, id is null. Rocket: {0}", this);
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
    }
}
