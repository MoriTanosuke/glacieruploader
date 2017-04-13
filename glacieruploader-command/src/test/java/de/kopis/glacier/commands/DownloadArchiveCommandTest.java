package de.kopis.glacier.commands;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.File;
import java.util.UUID;

import org.junit.Test;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;
import joptsimple.OptionSet;

public class DownloadArchiveCommandTest extends AbstractCommandTest {
    @Test
    public void exec() throws Exception {
        final String vaultName = UUID.randomUUID().toString();
        final String archiveId = UUID.randomUUID().toString();
        final String targetName = UUID.randomUUID().toString();
        final ArchiveTransferManager atm = createMock(ArchiveTransferManager.class);
        atm.download(vaultName, archiveId, new File(targetName));
        expectLastCall().times(1);
        replay(atm);

        final OptionSet options = optionParser.parse("--vault", vaultName,
                "--download", archiveId,
                "--target", targetName);
        new DownloadArchiveCommand(client, sqs, sns, atm).exec(options, optionParser);

        verify(atm);
    }

}