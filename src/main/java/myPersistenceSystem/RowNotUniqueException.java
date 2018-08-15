package myPersistenceSystem;

import java.sql.PreparedStatement;

public class RowNotUniqueException extends Exception {
    PreparedStatement statement;

    public RowNotUniqueException(String message, PreparedStatement statement) {
        super(message);
        this.statement = statement;
    }

    public RowNotUniqueException(String message, Throwable cause, PreparedStatement statement) {
        super(message, cause);
        this.statement = statement;
    }

    public RowNotUniqueException(Throwable cause, PreparedStatement statement) {
        super(cause);
        this.statement = statement;
    }
}
