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

import com.amazonaws.services.glacier.model.InitiateJobRequest;
import com.amazonaws.services.glacier.model.InitiateJobResult;
import de.kopis.glacier.parsers.GlacierUploaderOptionParser;
import joptsimple.OptionSet;
import org.apache.commons.configuration.MapConfiguration;
import org.easymock.Capture;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RequestArchivesListCommandTest extends AbstractCommandTest {
    @Test
    public void exec() throws Exception {
        final InitiateJobRequest jobRequest = createMock(InitiateJobRequest.class);
        final InitiateJobResult jobResult = new InitiateJobResult();
        jobResult.setJobId(UUID.randomUUID().toString());
        expect(client.initiateJob(isA(InitiateJobRequest.class))).andReturn(jobResult).times(1);
        replay(client);

        final OptionSet options = optionParser.parse("--vault", "vaultName", "--list-inventory");
        final RequestArchivesListCommand command = new RequestArchivesListCommand(client, sqs, sns);

        assertTrue(command.valid(options, optionParser));
        command.exec(options, optionParser);

        verify(client);
    }

    @Test
    public void testVaultOverrideBySystemEnvironment() {
        final String vaultName = UUID.randomUUID().toString();

        final Map<String, String> props = new HashMap<>();
        props.put("vault", vaultName);

        // override option parser and check if it's taking the default values from provided configuration
        final GlacierUploaderOptionParser optionParser = new GlacierUploaderOptionParser(new MapConfiguration(props));
        final OptionSet options = optionParser.parse("--list-inventory");

        Capture<? extends InitiateJobRequest> capturedJobRequest = newCapture();
        expect(client.initiateJob(capture(capturedJobRequest)))
                .andReturn(new InitiateJobResult())
                .times(1);
        replay(client);

        final RequestArchivesListCommand command = new RequestArchivesListCommand(client, sqs, sns);
        command.exec(options, optionParser);

        verify(client);
        // make sure the vault name from our config is used
        assertEquals(capturedJobRequest.getValue().getVaultName(), vaultName);
    }
}
