package io.humanode.humanodealert.exceptions;

public class HumanodeException extends RuntimeException {
    public HumanodeException() {
        super();
    }

    public HumanodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public HumanodeException(String message) {
        super(message);
    }

    public HumanodeException(Throwable cause) {
        super(cause);
    }
}
