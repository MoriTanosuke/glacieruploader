package de.kopis.glacier.commands;

/*-
 * #%L
 * glacieruploader-command
 * %%
 * Copyright (C) 2012 - 2017 Carsten Ringe
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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.UUID;

import org.junit.Test;
import joptsimple.OptionSet;

public class TreeHashArchiveCommandTest extends AbstractCommandTest {
    @Test
    public void canCalculateTreeHashForFile() throws Exception {
        final String content = UUID.randomUUID().toString();
        final File file = writeTemporaryFile(content);

        final OptionSet options = optionParser.parse("--calculate", file.getAbsolutePath());
        // TODO how to verify this call? output is only into log
        final TreeHashArchiveCommand command = new TreeHashArchiveCommand(client, sqs, sns);

        assertTrue(command.valid(options, optionParser));
        command.exec(options, optionParser);
    }
    @Test
    public void canNotCalculateTreeHashForMissingFile() throws Exception {
        final File file = new File("notexisting.txt");

        final OptionSet options = optionParser.parse("--calculate", file.getAbsolutePath());
        // TODO how to verify this call? output is only into log
        new TreeHashArchiveCommand(client, sqs, sns).exec(options, optionParser);
    }

}
