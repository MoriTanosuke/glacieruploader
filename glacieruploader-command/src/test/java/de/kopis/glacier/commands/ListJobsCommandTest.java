package de.kopis.glacier.commands;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

import org.junit.Test;
import com.amazonaws.services.glacier.model.GlacierJobDescription;
import com.amazonaws.services.glacier.model.ListJobsRequest;
import com.amazonaws.services.glacier.model.ListJobsResult;
import de.kopis.glacier.printers.JobPrinter;
import joptsimple.OptionSet;

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

        final OptionSet options = optionParser.parse();
        new ListJobsCommand(client, sqs, sns, new JobPrinter() {
            @Override
            public void printJob(final GlacierJobDescription job, final OutputStream o) {
                assertEquals(jobId, job.getJobId());
            }
        }).exec(options, optionParser);

        verify(client);
    }
}