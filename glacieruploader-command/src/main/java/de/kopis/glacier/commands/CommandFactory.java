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

import de.kopis.glacier.parsers.GlacierUploaderOptionParser;
import joptsimple.OptionSet;

import java.util.ArrayList;
import java.util.List;

public final class CommandFactory {

    private static List<AbstractCommand> commands = new ArrayList<>(9);
    private static AbstractCommand defaultCommand = null;

    private CommandFactory() {
        // do not instantiate
    }

    /**
     * Adds a command to the list of commands
     *
     * @param command {@link AbstractCommand}
     */
    public static void add(AbstractCommand command) {
        commands.add(command);
    }

    /**
     * Removes a command from the list of commands
     *
     * @param command {@link AbstractCommand}
     */
    public static void remove(AbstractCommand command) {
        commands.remove(command);
    }

    /**
     * Get the currently setted default command
     *
     * @return AbstractCommand
     */
    public static AbstractCommand getDefaultCommand() {
        return defaultCommand;
    }

    /**
     * Sets the default command. This is used when no other command is valid.
     *
     * @param command {@link AbstractCommand} to use as default
     */
    public static void setDefaultCommand(AbstractCommand command) {
        defaultCommand = command;
    }

    /**
     * Gets the first valid command, based on the options specified
     *
     * @param options      a complete {@link OptionSet}
     * @param optionParser a {@link GlacierUploaderOptionParser} to use on the given options
     * @return a valid {@link AbstractCommand}
     */
    public static AbstractCommand get(OptionSet options, GlacierUploaderOptionParser optionParser) {
        for (AbstractCommand command : commands) {
            if (command.valid(options, optionParser)) {
                return command;
            }
        }
        return defaultCommand;
    }

}
