package com.six.spacex.domain.id;

import com.six.spacex.domain.InvalidObjectStateException;

import java.util.UUID;

public record MissionId(UUID value) implements SpaceXId {

    public MissionId {
        if (value == null) {
            throw new InvalidObjectStateException("Mission ID cannot be null");
        }
    }
}
