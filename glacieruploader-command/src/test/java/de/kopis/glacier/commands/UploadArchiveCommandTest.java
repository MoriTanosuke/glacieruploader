package de.kopis.glacier.commands;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.junit.Test;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;
import com.amazonaws.services.glacier.transfer.UploadResult;
import joptsimple.OptionSet;

public class UploadArchiveCommandTest extends AbstractCommandTest {
    @Test
    public void canUploadFile() throws Exception {
        final String content = UUID.randomUUID().toString();
        final File file = writeTemporaryFile(content);

        final ArchiveTransferManager atm = createMock(ArchiveTransferManager.class);
        expect(atm.upload(eq("vaultName"), eq(file.getName()), eq(file))).andReturn(new UploadResult(UUID.randomUUID().toString())).times(1);
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