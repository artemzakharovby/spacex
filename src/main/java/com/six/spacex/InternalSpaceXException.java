package com.six.spacex;

import java.text.MessageFormat;

public class InternalSpaceXException extends RuntimeException {

    public InternalSpaceXException(String message, Object... params) {
        super(MessageFormat.format(message, params));
    }
}
