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

import org.apache.commons.lang3.Validate;
import com.amazonaws.services.glacier.AmazonGlacier;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManagerBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sqs.AmazonSQS;
import de.kopis.glacier.parsers.GlacierUploaderOptionParser;
import joptsimple.OptionSet;

public class DownloadArchiveCommand extends AbstractCommand {

    private ArchiveTransferManager atm;

    public DownloadArchiveCommand(AmazonGlacier client, AmazonSQS sqs, AmazonSNS sns) {
        this(client, sqs, sns, new ArchiveTransferManagerBuilder().withGlacierClient(client).withSqsClient(sqs).withSnsClient(sns).build());
    }

    public DownloadArchiveCommand(final AmazonGlacier client, final AmazonSQS sqs, final AmazonSNS sns, final ArchiveTransferManager atm) {
        super(client, sqs, sns);
        this.atm = atm;
    }

    private void download(final String vaultName, final String archiveId, final String targetFile) {
        final File downloadFile = new File(targetFile);
        download(vaultName, archiveId, downloadFile);
    }

    private void download(final String vaultName, final String archiveId, final File targetFile) {
        Validate.notNull(vaultName, "vaultName can not be null");
        Validate.notNull(archiveId, "archiveId can not be null");
        Validate.notNull(targetFile, "targetFile can not be null");
        log.info("Downloading archive " + archiveId + " from vault " + vaultName + "...");
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
