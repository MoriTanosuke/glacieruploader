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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import org.junit.Test;
import com.amazonaws.services.glacier.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.glacier.model.CompleteMultipartUploadResult;
import com.amazonaws.services.glacier.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.glacier.model.InitiateMultipartUploadResult;
import com.amazonaws.services.glacier.model.UploadMultipartPartRequest;
import com.amazonaws.services.glacier.model.UploadMultipartPartResult;
import joptsimple.OptionSet;

public class UploadMultipartArchiveCommandTest extends AbstractCommandTest {
    @Test
    public void testExec() throws IOException {
        final File tempFile = File.createTempFile("this is a test with whitespaces", ".txt");
        tempFile.deleteOnExit();
        try(FileWriter fw = new FileWriter(tempFile)) {
            fw.write(UUID.randomUUID().toString());
        }

        final OptionSet options = optionParser.parse("--vault", "dummy", "-m", tempFile.getAbsolutePath());

        final InitiateMultipartUploadResult initiateResult = new InitiateMultipartUploadResult();
        initiateResult.setUploadId(UUID.randomUUID().toString());
        expect(client.initiateMultipartUpload(isA(InitiateMultipartUploadRequest.class))).andReturn(initiateResult).times(1);
        expect(client.uploadMultipartPart(isA(UploadMultipartPartRequest.class))).andReturn(new UploadMultipartPartResult()).times(1);
        expect(client.completeMultipartUpload(isA(CompleteMultipartUploadRequest.class))).andReturn(new CompleteMultipartUploadResult()).times(1);
        replay(client, sqs, sns);

        new UploadMultipartArchiveCommand(client, sqs, sns).exec(options, optionParser);

        verify(client, sqs, sns);
    }
}
