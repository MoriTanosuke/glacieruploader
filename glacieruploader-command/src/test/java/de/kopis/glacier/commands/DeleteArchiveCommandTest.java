package de.kopis.glacier.commands;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.easymock.Capture;
import org.junit.Test;
import com.amazonaws.services.glacier.model.DeleteArchiveRequest;
import com.amazonaws.services.glacier.model.DeleteArchiveResult;
import joptsimple.OptionSet;

public class DeleteArchiveCommandTest extends AbstractCommandTest {
    @Test
    public void exec() throws Exception {
        final String vaultName = "dummyVaultName";
        final String archiveId = UUID.randomUUID().toString();

        final Capture<DeleteArchiveRequest> capturedRequest = newCapture();
        expect(client.deleteArchive(capture(capturedRequest))).andReturn(new DeleteArchiveResult()).times(1);
        replay(client);

        final OptionSet options = optionParser.parse("--vault", vaultName, "--delete", archiveId);
        new DeleteArchiveCommand(client, sqs, sns).exec(options, optionParser);

        verify(client);
        assertEquals(vaultName, capturedRequest.getValue().getVaultName());
        assertEquals(archiveId, capturedRequest.getValue().getArchiveId());
    }

}