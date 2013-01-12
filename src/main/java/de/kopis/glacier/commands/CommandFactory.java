package de.kopis.glacier.commands;

import java.util.ArrayList;

import joptsimple.OptionSet;
import de.kopis.glacier.parsers.GlacierUploaderOptionParser;

/*
 * #%L
 * uploader
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2012 Carsten Ringe
 * Copyright (C) 2013 Deux Huit Huit
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

public final class CommandFactory {

  private static ArrayList<AbstractCommand> commands = new ArrayList<AbstractCommand>(9);
  private static AbstractCommand defaultCommand = null;

  /**
   * Adds a command to the list of commands
   * 
   * @param command
   */
  public static void add(AbstractCommand command) {
    commands.add(command);
  }

  /**
   * Removes a command from the list of commands
   * 
   * @param command
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
   * @param command
   */
  public static void setDefaultCommand(AbstractCommand command) {
    defaultCommand = command;
  }

  /**
   * Gets the first valid command, based on the options specified
   * 
   * @param options
   * @param optionParser
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
