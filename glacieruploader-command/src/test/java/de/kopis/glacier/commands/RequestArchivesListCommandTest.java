package de.kopis.glacier.commands;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.UUID;

import org.junit.Test;
import com.amazonaws.services.glacier.model.InitiateJobRequest;
import com.amazonaws.services.glacier.model.InitiateJobResult;
import joptsimple.OptionSet;

public class RequestArchivesListCommandTest extends AbstractCommandTest {
    @Test
    public void exec() throws Exception {
        final InitiateJobRequest jobRequest = createMock(InitiateJobRequest.class);
        final InitiateJobResult jobResult = new InitiateJobResult();
        jobResult.setJobId(UUID.randomUUID().toString());
        expect(client.initiateJob(isA(InitiateJobRequest.class))).andReturn(jobResult).times(1);
        replay(client);

        final OptionSet options = optionParser.parse("--vault", "vaultName", "--list-inventory");
        new RequestArchivesListCommand(client, sqs, sns).exec(options, optionParser);

        verify(client);
    }

}