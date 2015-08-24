package de.kopis.glacier.printers;

import com.amazonaws.AmazonClientException;

public class CommandResult {

    public enum CommandResultStatus {
        SUCCESS,
        FAILURE,
        UNKNOWN
    }

    private final String message;
    private final CommandResultStatus status;
    private Exception exception;

    public CommandResult(CommandResultStatus failure, String s, AmazonClientException exception) {
        this(failure, s);
        this.exception = exception;
    }

    public CommandResult(CommandResultStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public Exception getException() {
        return exception;
    }

    public String getMessage() {
        return message;
    }

    public CommandResultStatus getStatus() {
        return status;
    }
}
