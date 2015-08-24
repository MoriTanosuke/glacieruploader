package de.kopis.glacier.commands;

/*
 * #%L
 * glacieruploader
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2012 - 2015 Carsten Ringe
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
