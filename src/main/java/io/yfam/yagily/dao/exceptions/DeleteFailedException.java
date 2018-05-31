package io.yfam.yagily.dao.exceptions;

public class DeleteFailedException extends RuntimeException {
    public DeleteFailedException(String table, int id) {
        super(String.format("Error while delete %s of id %d!", table, id));
    }
}
