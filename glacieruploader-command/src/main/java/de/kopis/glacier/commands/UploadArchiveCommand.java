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
import java.util.List;

import com.amazonaws.services.glacier.AmazonGlacier;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManagerBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sqs.AmazonSQS;
import de.kopis.glacier.parsers.GlacierUploaderOptionParser;
import joptsimple.OptionSet;

public class UploadArchiveCommand extends AbstractCommand {

    private final ArchiveTransferManager atm;

    public UploadArchiveCommand(final AmazonGlacier client, final AmazonSQS sqs, final AmazonSNS sns) {
        this(client, sqs, sns, new ArchiveTransferManagerBuilder()
                .withGlacierClient(client)
                .withSqsClient(sqs)
                .withSnsClient(sns)
                .build());
    }

    public UploadArchiveCommand(final AmazonGlacier client, final AmazonSQS sqs, final AmazonSNS sns, final ArchiveTransferManager atm) {
        super(client, sqs, sns);
        this.atm = atm;
    }

    public void upload(final String vaultName, final File uploadFile) {
        log.info("Starting to upload " + uploadFile + " to vault " + vaultName + "...");
        try {
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
