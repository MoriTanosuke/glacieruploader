package de.kopis.glacier.commands;

/*
 * #%L
 * glacieruploader-command
 * %%
 * Copyright (C) 2012 - 2016 Carsten Ringe
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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;
import de.kopis.glacier.parsers.GlacierUploaderOptionParser;
import joptsimple.OptionSet;

public class DownloadArchiveCommand extends AbstractCommand {

    public DownloadArchiveCommand(final URL endpoint, final File credentials) throws IOException {
        super(endpoint, credentials);
    }

    public void download(final String vaultName, final String archiveId, final String targetFile) {
        final File downloadFile = new File(targetFile);
        download(vaultName, archiveId, downloadFile);
    }

    public void download(final String vaultName, final String archiveId, final File targetFile) {
        log.info("Downloading archive " + archiveId + " from vault " + vaultName + "...");
        final ArchiveTransferManager atm = new ArchiveTransferManager(client, sqs, sns);
        atm.download(vaultName, archiveId, targetFile);
        log.info("Archive downloaded to " + targetFile);
    }

    @Override
    public void exec(OptionSet options, GlacierUploaderOptionParser optionParser) {
        final String vaultName = options.valueOf(optionParser.VAULT);
        final String archiveId = options.valueOf(optionParser.DOWNLOAD);
        final File targetFile = options.valueOf(optionParser.TARGET_FILE);
        this.download(vaultName, archiveId, targetFile);
    }

    @Override
    public boolean valid(OptionSet options, GlacierUploaderOptionParser optionParser) {
        return options.has(optionParser.DOWNLOAD) && options.has(optionParser.TARGET_FILE);
    }
}
