package io.yfam.yagily.dao.exceptions;

public class InsertFailedException extends RuntimeException {
    public InsertFailedException(String table) {
        super(String.format("Error while insert into table %s!", table));
    }
}
