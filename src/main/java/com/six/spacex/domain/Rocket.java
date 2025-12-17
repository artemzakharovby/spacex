package com.six.spacex.domain;

import com.six.spacex.domain.id.RocketId;

import java.util.Objects;
import java.util.Optional;

public class Rocket implements SpaceXObject {

    private final Optional<RocketId> rocketId;
    private final RocketStatus status;

    public Rocket(Optional<RocketId> rocketId) {
        this.rocketId = rocketId;
        this.status = RocketStatus.ON_GROUND;
    }

    public Optional<RocketId> getRocketId() {
        return rocketId;
    }

    public RocketStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Rocket rocket = (Rocket) o;
        return Objects.equals(rocketId, rocket.rocketId) && status == rocket.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rocketId, status);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Rocket{");
        sb.append("rocketId=").append(rocketId);
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }
}
