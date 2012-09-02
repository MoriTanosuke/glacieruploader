package de.kopis.glacier;

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

import com.amazonaws.services.glacier.model.DeleteArchiveRequest;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;

public class GlacierArchiveDownloader extends AbstractGlacierCommand {

  public GlacierArchiveDownloader(final File credentials) throws IOException {
    super(credentials);
  }

  public void download(final URL endpointUrl, final String vaultName, final String archiveId) {
    System.out.println("Downloading archive " + archiveId + " from vault " + vaultName + "...");
    client.setEndpoint(endpointUrl.toExternalForm());

    try {
      final File downloadFile = File.createTempFile("glacier-", ".dl");
      final ArchiveTransferManager atm = new ArchiveTransferManager(client, sqs, sns);
      atm.download(vaultName, archiveId, downloadFile);
      System.out.println("Archive downloaded to " + downloadFile);
    } catch (final IOException e) {
      System.err.println("Can not download archive " + archiveId + " from vault " + vaultName + ".");
      e.printStackTrace();
    }
  }

  public void delete(final URL endpointUrl, final String vaultName, final String archiveId) {
    System.out.println("Deleting archive " + archiveId + " from vault " + vaultName + "...");
    final DeleteArchiveRequest deleteArchiveRequest = new DeleteArchiveRequest(vaultName, archiveId);
    client.deleteArchive(deleteArchiveRequest);
    System.out.println("Archive " + archiveId + " deleted from vault " + vaultName + ".");
  }

}
