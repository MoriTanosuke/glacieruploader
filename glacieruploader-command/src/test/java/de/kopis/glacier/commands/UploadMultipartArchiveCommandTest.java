package de.kopis.glacier.commands;

/*
 * #%L
 * glacieruploader
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2012 - 2014 Carsten Ringe
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

import com.amazonaws.services.glacier.model.*;
import joptsimple.OptionSet;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class UploadMultipartArchiveCommandTest extends AbstractCommandTest {
    private static final Logger LOG = LoggerFactory.getLogger(UploadMultipartArchiveCommand.class);

    @Test
    public void testExec() throws IOException {
        final File tempFile = File.createTempFile("this is a test with whitespaces", ".txt");
        tempFile.deleteOnExit();
        try(FileWriter fw = new FileWriter(tempFile)) {
            fw.write(UUID.randomUUID().toString());
        }

        final OptionSet options = optionParser.parse("--vault", "dummy", "-m", tempFile.getAbsolutePath(), "--partsize", "1234");

        final InitiateMultipartUploadResult initiateResult = new InitiateMultipartUploadResult();
        initiateResult.setUploadId(UUID.randomUUID().toString());
        expect(client.initiateMultipartUpload(isA(InitiateMultipartUploadRequest.class))).andReturn(initiateResult).times(1);
        expect(client.uploadMultipartPart(isA(UploadMultipartPartRequest.class))).andReturn(new UploadMultipartPartResult()).times(1);
        expect(client.completeMultipartUpload(isA(CompleteMultipartUploadRequest.class))).andReturn(new CompleteMultipartUploadResult()).times(1);
        replay(client, sqs, sns);

        final UploadMultipartArchiveCommand command = new UploadMultipartArchiveCommand(client, sqs, sns);

        assertTrue(command.valid(options, optionParser));
        command.exec(options, optionParser);

        verify(client, sqs, sns);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMaximumParts() {
        final long partSize = 10;
        final String filePath = "doesnotexist.txt";
        final File dummyFile = new File(filePath);
        dummyFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(dummyFile)) {
            for (int parts = 0; parts <= (UploadMultipartArchiveCommand.MAX_PARTS + 1); parts++) {
                for (int i = 0; i < partSize; i++) {
                    out.write('.');
                }
            }
            LOG.info("File {} written, length {}", dummyFile.getAbsolutePath(), dummyFile.length());
        } catch (IOException e) {
            LOG.error("Can not create dummy file", e);
            fail("Can not create dummy file: " + e.getMessage());
        }

        final OptionSet options = optionParser.parse("--vault", "dummy",
                "-m", filePath,
                "--partsize", Long.toString(partSize));

        final UploadMultipartArchiveCommand command = new UploadMultipartArchiveCommand(client, sqs, sns);

        assertTrue(command.valid(options, optionParser));
        command.exec(options, optionParser);
    }
}
