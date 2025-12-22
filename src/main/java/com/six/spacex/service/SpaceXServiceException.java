package com.six.spacex.service;

import java.text.MessageFormat;

public class SpaceXServiceException extends RuntimeException {

    public SpaceXServiceException(String message, Object... params) {
        super(MessageFormat.format(message, params));
    }
}
