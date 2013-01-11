package de.kopis.glacier.commands;

/*
 * #%L
 * uploader
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2012 Carsten Ringe
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

import joptsimple.OptionSet;

import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;

import de.kopis.glacier.parsers.GlacierUploaderOptionParser;

public class UploadArchiveCommand extends AbstractCommand {

  public UploadArchiveCommand(final URL endpoint, final File credentials) throws IOException {
    super(endpoint, credentials);
  }

  public void upload(final String vaultName, final File uploadFile) {
    log.info("Starting to upload " + uploadFile + " to vault " + vaultName + "...");
    try {
      log.info("Starting upload of " + uploadFile);
      final ArchiveTransferManager atm = new ArchiveTransferManager(client, sqs, sns);
      final String archiveId = atm.upload(vaultName, uploadFile.getName(), uploadFile).getArchiveId();
      log.info("Uploaded archive " + archiveId);
    } catch (final IOException e) {
      System.err.println("Something went wrong while uploading " + uploadFile + ".");
      e.printStackTrace();
    }
  }

	@Override
	public void exec(OptionSet options, GlacierUploaderOptionParser optionParser) {
		final String vaultName = options.valueOf(optionParser.VAULT);
		final File uploadFile = options.valueOf(optionParser.UPLOAD);
		this.upload(vaultName, uploadFile);
	}
	
	@Override
	public boolean valid(OptionSet options, GlacierUploaderOptionParser optionParser) {
		return options.has(optionParser.UPLOAD) && options.has(optionParser.VAULT);
	}
}
