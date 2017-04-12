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
import java.util.List;

import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;
import de.kopis.glacier.parsers.GlacierUploaderOptionParser;
import joptsimple.OptionSet;

public class UploadArchiveCommand extends AbstractCommand {

    public UploadArchiveCommand(final URL endpoint, final File credentials) throws IOException {
        super(endpoint, credentials);
    }

    public void upload(final String vaultName, final File uploadFile) {
        log.info("Starting to upload " + uploadFile + " to vault " + vaultName + "...");
        try {
            final ArchiveTransferManager atm = new ArchiveTransferManager(client, sqs, sns);
            final String archiveId = atm.upload(vaultName, uploadFile.getName(), uploadFile).getArchiveId();
            log.info("Uploaded archive " + archiveId);
        } catch (final IOException e) {
            log.error("Something went wrong while uploading " + uploadFile + vaultName + "to vault " + ".", e);
        }
    }

    @Override
    public void exec(OptionSet options, GlacierUploaderOptionParser optionParser) {
        final String vaultName = options.valueOf(optionParser.VAULT);
        final List<File> optionsFiles = options.valuesOf(optionParser.UPLOAD);
        final List<String> nonOptions = options.nonOptionArguments();
        final List<File> files = optionParser.mergeNonOptionsFiles(optionsFiles, nonOptions);

        for (File uploadFile : files) {
            this.upload(vaultName, uploadFile);
        }
    }

    @Override
    public boolean valid(OptionSet options, GlacierUploaderOptionParser optionParser) {
        return options.has(optionParser.UPLOAD);
    }
}
