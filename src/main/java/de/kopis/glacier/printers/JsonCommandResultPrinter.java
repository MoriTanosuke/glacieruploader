package de.kopis.glacier.printers;

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

import de.kopis.glacier.commands.CommandResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Optional;

public class JsonCommandResultPrinter {
    private static final Log log = LogFactory.getLog(JsonCommandResultPrinter.class);

    public void print(OutputStream out, CommandResult result) {
        //TODO create JSON representation of CommandResult
        final PrintWriter pw = new PrintWriter(out);
        pw.write("{status: '" + result.getStatus() + "', message: '" + result.getMessage() + "'");

        final Optional<String> originalMessage = result.getOriginalMessage();
        if (originalMessage.isPresent()) {
            pw.write(", originalMessage: " + originalMessage.get());
        }
        pw.write("}");
        pw.flush();
    }
}
