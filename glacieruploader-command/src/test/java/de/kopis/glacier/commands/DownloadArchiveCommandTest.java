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
