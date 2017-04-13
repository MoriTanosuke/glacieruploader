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

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.glacier.AmazonGlacier;
import com.amazonaws.services.glacier.model.AbortMultipartUploadRequest;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sqs.AmazonSQS;
import de.kopis.glacier.parsers.GlacierUploaderOptionParser;
import joptsimple.OptionSet;

/**
 * Abort Multipart Archive Upload Command.
 *
 * @author Kendal Montgomery theWizK@yahoo.com
 * @version 1.0
 */
public class AbortMultipartArchiveUploadCommand extends AbstractCommand {

    public AbortMultipartArchiveUploadCommand(AmazonGlacier client, AmazonSQS sqs, AmazonSNS sns) {
        super(client, sqs, sns);
    }

    /* (non-Javadoc)
     * @see de.kopis.glacier.commands.AbstractCommand#exec(joptsimple.OptionSet, de.kopis.glacier.parsers.GlacierUploaderOptionParser)
     */
    @Override
    public void exec(OptionSet options, GlacierUploaderOptionParser optionParser) {
        final String vaultName = options.valueOf(optionParser.VAULT);
        final String uploadId = options.valueOf(optionParser.ABORT_UPLOAD);
        try {
            abortUpload(vaultName, uploadId);
        } catch (AmazonServiceException e) {
            log.error("Something went wrong at Amazon while aborting the multipart upload with id " + uploadId + ". " + e.getLocalizedMessage(), e);
        } catch (AmazonClientException e) {
            log.error("Something went wrong with the Amazon Client. " + e.getLocalizedMessage(), e);
        }
    }

    /* (non-Javadoc)
     * @see de.kopis.glacier.commands.AbstractCommand#valid(joptsimple.OptionSet, de.kopis.glacier.parsers.GlacierUploaderOptionParser)
     */
    @Override
    public boolean valid(OptionSet options, GlacierUploaderOptionParser optionParser) {
        return options.has(optionParser.ABORT_UPLOAD) && options.hasArgument(optionParser.ABORT_UPLOAD);
    }

    private void abortUpload(final String vaultName, final String uploadId) {
        final AbortMultipartUploadRequest abortRequest = new AbortMultipartUploadRequest().withUploadId(uploadId).withVaultName(vaultName);
        log.info("Aborting upload to vault " + vaultName + " with upload id " + uploadId + ".");
        client.abortMultipartUpload(abortRequest);
        log.info("Upload aborted successfully.");
    }

}
