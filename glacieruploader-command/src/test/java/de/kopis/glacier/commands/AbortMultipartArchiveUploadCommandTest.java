package de.kopis.glacier.commands;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.Test;
import com.amazonaws.services.glacier.model.AbortMultipartUploadRequest;
import com.amazonaws.services.glacier.model.AbortMultipartUploadResult;
import joptsimple.OptionSet;

public class AbortMultipartArchiveUploadCommandTest extends AbstractCommandTest {
    @Test
    public void exec() throws Exception {
        expect(client.abortMultipartUpload(isA(AbortMultipartUploadRequest.class))).andReturn(new AbortMultipartUploadResult()).times(1);
        replay(client);

        final OptionSet options = optionParser.parse();
        new AbortMultipartArchiveUploadCommand(client, sqs, sns).exec(options, optionParser);

        verify(client);
    }

}