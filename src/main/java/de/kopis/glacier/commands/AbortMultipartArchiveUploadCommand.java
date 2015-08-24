/**
 *
 */
package de.kopis.glacier.commands;

/*
 * #%L
 * glacieruploader
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2012 - 2014 Carsten Ringe
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

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.glacier.model.AbortMultipartUploadRequest;
import de.kopis.glacier.parsers.GlacierUploaderOptionParser;
import joptsimple.OptionSet;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import static de.kopis.glacier.commands.CommandResult.CommandResultStatus;

/**
 * Abort Multipart Archive Upload Command.
 *
 * @author Kendal Montgomery <theWizK@yahoo.com>
 * @author Carsten Ringe <carsten@kopis.de>
 * @version 1.1
 */
public class AbortMultipartArchiveUploadCommand extends AbstractCommand {

    public AbortMultipartArchiveUploadCommand(final URL endpoint, final File credentials) throws IOException {
        super(endpoint, credentials);
    }

    @Override
    public Optional<CommandResult> exec(OptionSet options, GlacierUploaderOptionParser optionParser) {
        final String vaultName = options.valueOf(optionParser.VAULT);
        final String uploadId = options.valueOf(optionParser.ABORT_UPLOAD);
        Optional<CommandResult> result = Optional.empty();
        try {
            result = abortUpload(vaultName, uploadId);
        } catch (AmazonServiceException e) {
            log.error("Something went wrong at Amazon while aborting the multipart upload with id " + uploadId + ". " + e.getLocalizedMessage(), e);
            result = Optional.of(new CommandResult(CommandResultStatus.FAILURE, e.getMessage(), Optional.empty(), Optional.of(e)));
        } catch (AmazonClientException e) {
            log.error("Something went wrong with the Amazon Client. " + e.getLocalizedMessage(), e);
            result = Optional.of(new CommandResult(CommandResultStatus.FAILURE, e.getMessage(), Optional.empty(), Optional.of(e)));
        }

        return result;
    }

    /* (non-Javadoc)
     * @see de.kopis.glacier.commands.AbstractCommand#valid(joptsimple.OptionSet, de.kopis.glacier.parsers.GlacierUploaderOptionParser)
     */
    @Override
    public boolean valid(OptionSet options, GlacierUploaderOptionParser optionParser) {
        return options.has(optionParser.ABORT_UPLOAD) && options.hasArgument(optionParser.ABORT_UPLOAD);
    }

    public Optional<CommandResult> abortUpload(final String vaultName, final String uploadId) {
        final AbortMultipartUploadRequest abortRequest = new AbortMultipartUploadRequest().withUploadId(uploadId).withVaultName(vaultName);
        log.info("Aborting upload to vault " + vaultName + " with upload id " + uploadId + ".");
        client.abortMultipartUpload(abortRequest);
        log.info("Assumed success!");
        return Optional.of(new CommandResult(CommandResultStatus.SUCCESS, "Multipart Upload aborted", Optional.empty()));
    }

}
