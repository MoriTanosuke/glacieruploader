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

import com.amazonaws.services.glacier.model.GlacierJobDescription;
import com.amazonaws.services.glacier.model.ListJobsRequest;
import com.amazonaws.services.glacier.model.ListJobsResult;
import de.kopis.glacier.printers.JobPrinter;
import joptsimple.OptionSet;
import org.junit.Test;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class ListJobsCommandTest extends AbstractCommandTest {
    @Test
    public void exec() {
        final String jobId = UUID.randomUUID().toString();

        final ListJobsResult jobResult = new ListJobsResult();
        final GlacierJobDescription jobDescription = new GlacierJobDescription();
        jobDescription.setJobId(jobId);
        jobDescription.setCompleted(true);
        jobResult.setJobList(Arrays.asList(jobDescription));
        expect(client.listJobs(isA(ListJobsRequest.class))).andReturn(jobResult).times(1);
        replay(client);

        final OptionSet options = optionParser.parse("--vault", "vaultName", "--list-jobs", UUID.randomUUID().toString());
        final ListJobsCommand command = new ListJobsCommand(client, sqs, sns, new JobPrinter() {
            @Override
            public void printJob(final GlacierJobDescription job, final OutputStream o) {
                assertEquals(jobId, job.getJobId());
            }
        });
        assertTrue(command.valid(options, optionParser));
        command.exec(options, optionParser);

        verify(client);
    }

    @Test
    public void execWithBlankVault() {
        final OptionSet options = optionParser.parse("--list-jobs", UUID.randomUUID().toString());
        final ListJobsCommand command = new ListJobsCommand(client, sqs, sns);
        assertFalse(command.valid(options, optionParser));
    }
}
