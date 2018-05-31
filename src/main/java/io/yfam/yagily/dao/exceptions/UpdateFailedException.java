package io.yfam.yagily.dao.exceptions;

public class UpdateFailedException extends RuntimeException {
    public UpdateFailedException(String table, int id) {
        super(String.format("Error while update %s of id %d!", table, id));
    }
}
