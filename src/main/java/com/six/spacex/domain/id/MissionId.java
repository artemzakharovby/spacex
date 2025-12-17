package com.six.spacex.domain.id;

import com.six.spacex.domain.InvalidObjectStateException;

import java.util.UUID;

public record MissionId(UUID id) implements SpaceXId {

    public MissionId {
        if (id == null) {
            throw new InvalidObjectStateException("Mission ID cannot be null");
        }
    }
}
