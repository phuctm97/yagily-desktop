package io.yfam.yagily.dao.exceptions;

public class CouldNotRetrieveGeneratedIdException extends RuntimeException {
    public CouldNotRetrieveGeneratedIdException(String table) {
        super(String.format("Could not retrieve generated id after insert into table %s!", table));
    }
}
