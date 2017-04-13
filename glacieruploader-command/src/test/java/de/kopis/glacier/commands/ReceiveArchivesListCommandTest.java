package de.kopis.glacier.commands;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.OutputStream;
import java.util.UUID;

import org.json.JSONException;
import org.junit.Test;
import com.amazonaws.services.glacier.model.GetJobOutputRequest;
import com.amazonaws.services.glacier.model.GetJobOutputResult;
import com.amazonaws.util.StringInputStream;
import de.kopis.glacier.printers.VaultInventoryPrinter;
import joptsimple.OptionSet;

public class ReceiveArchivesListCommandTest extends AbstractCommandTest {
    @Test
    public void exec() throws Exception {
        final String content = UUID.randomUUID().toString();
        final GetJobOutputResult jobResult = new GetJobOutputResult();
        jobResult.setBody(new StringInputStream(content));
        expect(client.getJobOutput(isA(GetJobOutputRequest.class))).andReturn(jobResult).times(1);
        replay(client);

        final OptionSet options = optionParser.parse("--vault", "vaultName");
        new ReceiveArchivesListCommand(client, sqs, sns, new VaultInventoryPrinter() {
            @Override
            public void printInventory(final OutputStream out) throws JSONException {
                assertEquals(content, getInventory());
            }
        }).exec(options, optionParser);

        verify(client);
    }

}