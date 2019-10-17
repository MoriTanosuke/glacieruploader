package de.kopis.glacier.commands;

/*
 * #%L
 * glacieruploader-command
 * %%
 * Copyright (C) 2012 - 2016 Carsten Ringe
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.kopis.glacier.parsers.GlacierUploaderOptionParser;
import joptsimple.OptionSet;

/**
 * Base class for all commands.
 * 
 */
public abstract class AbstractCommand {
    protected final Logger log;

    public AbstractCommand() {
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * Called to execute the command.
     * @param options
     * @param optionParser
     */
    public abstract void exec(OptionSet options, GlacierUploaderOptionParser optionParser);

    /**
     * Called to ask the command if it's valid for the supplied arguments.
     * 
     * @param options
     * @param optionParser
     * @return
     */
    public abstract boolean valid(OptionSet options, GlacierUploaderOptionParser optionParser);

    /**
     * Called to ask the command to verify if the parameters are valid for the command.
     * 
     * @param options
     * @param optionParser
     * @throws IllegalArgumentException
     */
    public void verifyArguments(OptionSet options, GlacierUploaderOptionParser optionParser) throws IllegalArgumentException {
        
    }
}