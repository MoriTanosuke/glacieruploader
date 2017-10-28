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

import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;
import com.amazonaws.services.glacier.transfer.UploadResult;
import joptsimple.OptionSet;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UploadArchiveCommandTest extends AbstractCommandTest {
    @Test
    public void canUploadFile() throws Exception {
        final String content = UUID.randomUUID().toString();
        final File file = writeTemporaryFile(content);

        final ArchiveTransferManager atm = createMock(ArchiveTransferManager.class);
        expect(atm.upload(eq("-"), eq("vaultName"),
                eq(file.getName()), eq(file),
                isA(ProgressListener.class)
        )).andReturn(new UploadResult(UUID.randomUUID().toString())).times(1);
        replay(atm);

        final OptionSet options = optionParser.parse("--vault", "vaultName", "--upload", file.getAbsolutePath());
        new UploadArchiveCommand(client, sqs, sns, atm).exec(options, optionParser);

        verify(atm);
    }

    @Test
    public void canValidateOptions() throws IOException {
        final String content = UUID.randomUUID().toString();
        final File file = writeTemporaryFile(content);

        final ArchiveTransferManager atm = createMock(ArchiveTransferManager.class);

        final UploadArchiveCommand command = new UploadArchiveCommand(client, sqs, sns, atm);

        final OptionSet validOptions = optionParser.parse("--vault", "vaultName", "--upload", file.getAbsolutePath());
        assertTrue(command.valid(validOptions, optionParser));
        // omit the --upload option, which makes the command line invalid for this command
        final OptionSet invalidOptions = optionParser.parse("--vault", "vaultName");
        assertFalse(command.valid(invalidOptions, optionParser));
    }

}
