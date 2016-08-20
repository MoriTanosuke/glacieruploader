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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import de.kopis.glacier.parsers.GlacierUploaderOptionParser;
import joptsimple.OptionSet;

public class HelpCommand extends AbstractCommand {

    public HelpCommand() throws IOException {
        super(null, null);
    }

    public HelpCommand(final URL endpoint, final File credentials) throws IOException {
        super(endpoint, credentials);
    }

    @Override
    public void exec(OptionSet options, GlacierUploaderOptionParser optionParser) {
        if (!options.has(optionParser.HELP)) {
            log.info("Ooops, can't determine what you want to do. Check your options. " + System.getProperty("line.separator") +
                    "Do not forget that --vault and --endpoint are mandatory for all commands." + System.getProperty("line.separator"));
        }
        try {
            optionParser.printHelpOn(System.out);
        } catch (final IOException e) {
            log.error("Can not print help", e);
        }
    }

    @Override
    public boolean valid(OptionSet options, GlacierUploaderOptionParser optionParser) {
        return options.has(optionParser.HELP);
    }

}
