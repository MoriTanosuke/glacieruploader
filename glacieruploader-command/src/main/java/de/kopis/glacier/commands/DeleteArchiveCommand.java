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

import org.apache.commons.lang3.Validate;
import com.amazonaws.services.glacier.AmazonGlacier;
import com.amazonaws.services.glacier.model.DeleteArchiveRequest;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sqs.AmazonSQS;
import de.kopis.glacier.parsers.GlacierUploaderOptionParser;
import joptsimple.OptionSet;

public class DeleteArchiveCommand extends AbstractGlacierCommand {

    public DeleteArchiveCommand(AmazonGlacier client, AmazonSQS sqs, AmazonSNS sns) {
        super(client, sqs, sns);
    }

    private void delete(final String vaultName, final String archiveId) {
        Validate.notNull(vaultName, "vaultName can not be null");
        Validate.notNull(archiveId, "archiveId can not be null");
        log.info("Deleting archive {} from vault {}...", archiveId, vaultName);

        final DeleteArchiveRequest deleteRequest = new DeleteArchiveRequest(vaultName, archiveId);
        client.deleteArchive(deleteRequest);

        log.info("Archive {} deletion started from vault {}.", archiveId, vaultName);
    }

    @Override
    public void exec(OptionSet options, GlacierUploaderOptionParser optionParser) {
        final String vaultName = options.valueOf(optionParser.vault);
        final String archiveId = options.valueOf(optionParser.deleteArchive);
        this.delete(vaultName, archiveId);
    }

    @Override
    public boolean valid(OptionSet options, GlacierUploaderOptionParser optionParser) {
        return options.has(optionParser.deleteArchive) && options.hasArgument(optionParser.deleteArchive);
    }

}
