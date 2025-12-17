package com.six.spacex.domain;

import com.six.spacex.domain.id.RocketId;

import java.util.Objects;
import java.util.Optional;

public class Rocket implements SpaceXObject {

    private final Optional<RocketId> id;
    private final String name;
    private final RocketStatus status;

    public Rocket(Optional<RocketId> id, String name) {
        this(id, name, RocketStatus.ON_GROUND);
    }

    private Rocket(Optional<RocketId> id, String name, RocketStatus status) {
        this.id = id;
        this.name = name;
        this.status = status;
        validate();
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Rocket rocket = (Rocket) o;
        return Objects.equals(id, rocket.id) && Objects.equals(name, rocket.name) && status == rocket.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Rocket{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }

    private void validate() {
        if (id == null) {
            throw new InvalidObjectStateException("Rocket ID cannot be null, rocket: {0}", this);
        }
        if (name == null || name.isBlank()) {
            throw new InvalidObjectStateException("Rocket name cannot be null or blank, rocket: {0}", this);
        }
        if (status == null) {
            throw new InvalidObjectStateException("Rocket status cannot be null, rocket: {0}", this);
        }
    }
}
