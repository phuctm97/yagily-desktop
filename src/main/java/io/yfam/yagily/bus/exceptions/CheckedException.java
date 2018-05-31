package io.yfam.yagily.bus.exceptions;

public class CheckedException extends RuntimeException {
    public CheckedException(String message) {
        super(message);
    }

    public CheckedException() {
    }
}
