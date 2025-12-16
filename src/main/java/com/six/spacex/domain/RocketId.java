package com.six.spacex.domain;

import java.util.UUID;

public record RocketId(UUID id) {

    public RocketId {
        if (id == null) {
            throw new InvalidObjectStateException("Rocket ID cannot be null");
        }
    }
}
