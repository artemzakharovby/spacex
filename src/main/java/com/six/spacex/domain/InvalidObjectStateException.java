package com.six.spacex.domain;

import java.text.MessageFormat;

public class InvalidObjectStateException extends RuntimeException {

    public InvalidObjectStateException(String message, Object... params) {
        super(MessageFormat.format(message, params));
    }
}
