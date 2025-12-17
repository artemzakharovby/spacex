package com.six.spacex.domain.id;

import com.six.spacex.domain.InvalidObjectStateException;

import java.util.UUID;

public record RocketId(UUID id) implements SpaceXId {

    public RocketId {
        if (id == null) {
            throw new InvalidObjectStateException("Rocket ID cannot be null");
        }
    }
}
