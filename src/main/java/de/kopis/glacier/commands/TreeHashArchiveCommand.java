package de.kopis.glacier.commands;

/*
 * #%L
 * uploader
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2012 - 2013 Carsten Ringe
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.	If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.amazonaws.services.glacier.TreeHashGenerator;
import de.kopis.glacier.parsers.GlacierUploaderOptionParser;
import joptsimple.OptionSet;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class TreeHashArchiveCommand extends AbstractCommand {

    public TreeHashArchiveCommand(final URL endpoint, final File credentials) throws IOException {
        super(endpoint, credentials);
    }

    public CommandResult calculateTreeHash(File file) {
        CommandResult result = null;
        if (file.exists()) {
            String hash = TreeHashGenerator.calculateTreeHash(file);
            log.info(hash);
            result = new CommandResult(CommandResult.CommandResultStatus.SUCCESS, "TreeHash created: " + hash, Optional.empty());
        } else {
            String msg = String.format("File '%s' not found", file.getAbsolutePath());
            log.error(msg);
            result = new CommandResult(CommandResult.CommandResultStatus.FAILURE, "Can not create vault: " + msg, Optional.empty());
        }
        return result;
    }

    @Override
    public Optional<CommandResult> exec(OptionSet options, GlacierUploaderOptionParser optionParser) {
        final File file = options.valueOf(optionParser.CALCULATE_HASH);
        return Optional.ofNullable(this.calculateTreeHash(file));
    }

    @Override
    public boolean valid(OptionSet options, GlacierUploaderOptionParser optionParser) {
        return options.has(optionParser.CALCULATE_HASH);
    }

}
