package com.six.spacex.service;

import com.six.spacex.domain.id.SpaceXId;

import java.text.MessageFormat;
import java.util.function.Supplier;

public class SpaceXServiceException extends RuntimeException {

    public SpaceXServiceException(String message, Object... params) {
        super(MessageFormat.format(message, params));
    }

    public static SpaceXServiceException notFound(String name, SpaceXId id) {
        return new SpaceXServiceException("There is no {0} with ID {1}", name, id);
    }
}
