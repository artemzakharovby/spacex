package com.six.spacex.domain;

import com.six.spacex.domain.id.MissionId;
import com.six.spacex.domain.id.RocketId;

import java.util.Objects;
import java.util.Optional;

public class Rocket implements SpaceXObject {

    private final Optional<RocketId> id;
    private final String name;
    private final RocketStatus status;
    private final Optional<MissionId> missionId;

    public Rocket(Optional<RocketId> id, String name) {
        this(id, name, RocketStatus.ON_GROUND, Optional.empty());
    }

    private Rocket(Optional<RocketId> id, String name, RocketStatus status, Optional<MissionId> missionId) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.missionId = missionId;
        validate();
    }

    public Rocket markAsInSpace() {
        validateBeforeGoingToSpace();
        return new Rocket(id, name, RocketStatus.IN_SPACE, missionId);
    }

    public Rocket markAsRepaired() {
        if (status != RocketStatus.IN_REPAIR) {
            throw new InvalidObjectStateException(
                    "Rocket cannot be marked as repaired because of invalid status. Rocket: {0}", this
            );
        }
        return new Rocket(id, name, RocketStatus.ON_GROUND, missionId);
    }

    public Rocket markAsRepairing() {
        if (status != RocketStatus.ON_GROUND) {
            throw new InvalidObjectStateException("Rocket cannot be repaired because of invalid status. Rocket: {0}", this);
        }
        return new Rocket(id, name, RocketStatus.IN_REPAIR, missionId);
    }

    public Rocket markAsAssigned(MissionId missionId) {
        validateBeforeAssignment(missionId);
        return new Rocket(id, name, status, Optional.of(missionId));
    }

    public Optional<RocketId> getId() {
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
        if (status != RocketStatus.ON_GROUND) {
            throw new InvalidObjectStateException("Rocket cannot be in space because of invalid status. Rocket: {0}", this);
        }
        if (missionId.isEmpty()) {
            throw new InvalidObjectStateException("Rocket cannot be in space because there is no mission. Rocker: {0}", this);
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
        if (id == null || id.isEmpty()) {
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
